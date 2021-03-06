package org.apache.cassandra.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.ReadCommand;
import org.apache.cassandra.db.ReadResponse;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.db.Table;
import org.apache.cassandra.net.IAsyncCallback;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.SimpleCondition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReadCallback<T> implements IAsyncCallback {

	protected static final Logger logger = LoggerFactory.getLogger( ReadCallback.class );
	private final IReadCommand command;
	private final int blockfor;
	private final IResponseResolver<T> resolver;
	private final long startTime;
	final List<InetAddress> endpoints;
	protected final SimpleCondition condition=new SimpleCondition();
	protected final AtomicInteger received=new AtomicInteger(0);
	
	public ReadCallback(IResponseResolver<T> resolver,
			ConsistencyLevel consistency_level, IReadCommand command,
			List<InetAddress> endpoints) {
		this.command=command;
		this.blockfor=determineBlockFor(consistency_level,command.getKeyspace());
		this.resolver=resolver;
		this.startTime=System.currentTimeMillis();
		boolean repair=randomlyReadRepair();
		this.endpoints=repair||resolver instanceof RowRepairResolver
				?endpoints
				:preferredEndpoints(endpoints);
        if (logger.isDebugEnabled())
            logger.debug(String.format("Blockfor/repair is %s/%s; setting up requests to %s",
                                       blockfor, repair, StringUtils.join(this.endpoints, ",")));
	}

	protected List<InetAddress> preferredEndpoints(List<InetAddress> endpoints){
		return endpoints.subList(0, Math.min(endpoints.size(), blockfor));
	}
	
	public int determineBlockFor(ConsistencyLevel consistency_level,
			String table) {
		switch(consistency_level){
			case ONE:
				return 1;
			case TWO:
				return 2;
			case THREE:
				return 3;
			case QUORUM:
				return (Table.open(table).getReplicationStrategy().getReplicationFactor()/2);
			case ALL:
				return Table.open(table).getReplicationStrategy().getReplicationFactor();
			default:
				throw new UnsupportedOperationException("invalid consistency level: " + consistency_level);
		}
	}

	private boolean randomlyReadRepair() {
		if(resolver instanceof RowDigestResolver){
			assert command instanceof ReadCommand:command;
			String table=((RowDigestResolver)resolver).table;
			String columnFamily=((ReadCommand)command).getColumnFamilyName();
			CFMetaData cfmd=Schema.instance.getTableMetaData(table).get(columnFamily);
			return cfmd.getReadRepairChance()>FBUtilities.threadLocalRandom().nextDouble();
		}
		return false;
	}

	@Override
	public boolean isLatencyForSnitch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void response(Message msg) {
		// TODO Auto-generated method stub
		
	}

	public void assureSufficientLiveNodes() throws UnavailableException {
		if(endpoints.size()<blockfor){
            logger.debug("Live nodes {} do not satisfy ConsistencyLevel ({} required)",
                    StringUtils.join(endpoints, ", "), blockfor);
            throw new UnavailableException();			
		}
	}

	public T get() throws TimeoutException, IOException,DigestMismatchException {
		long timeout=DatabaseDescriptor.getRpcTimeout()-(System.currentTimeMillis()-startTime);
		boolean success;
		try{
			success=condition.await(timeout,TimeUnit.MILLISECONDS);
		}catch(InterruptedException ex){
			throw new AssertionError(ex);
		}
		if(!success){
			StringBuilder sb=new StringBuilder("");
			for(Message message:resolver.getMessages()){
				sb.append(message.getFrom()).append(",");
			}
			throw new TimeoutException("Operation timed out - received only " + received.get() + " responses from " + sb.toString() + " .");
		}
		return blockfor == 1 ? resolver.getData() : resolver.resolve();
	}

	protected boolean waitingFor(ReadResponse response){
		return true;
	}
	
	public void response(ReadResponse result) {
		((RowDigestResolver)resolver).injectPreProcessed(result);
		int n=waitingFor(result)
			 ?received.incrementAndGet()
			 :received.get();
		
	}

}
