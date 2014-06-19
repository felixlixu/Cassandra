package org.apache.cassandra.service;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.cassandra.net.Message;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.UnavailableException;

public class AbstractWriteResponseHandler implements IWriteResponseHandler {

	private final long startTime;
	private final ConsistencyLevel consistencyLevel;
	private final List<InetAddress> writeEndpoints;

	public AbstractWriteResponseHandler(List<InetAddress> writeEndpoints,
			ConsistencyLevel consistencyLevel) {
		startTime=System.currentTimeMillis();
		this.consistencyLevel=consistencyLevel;
		this.writeEndpoints=writeEndpoints;
	}

	@Override
	public void get() throws TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assureSufficientLiveNodes() throws UnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void response(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLatencyForSnitch() {
		// TODO Auto-generated method stub
		return false;
	}

}
