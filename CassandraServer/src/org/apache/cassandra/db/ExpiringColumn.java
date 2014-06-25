package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.cassandra.io.IColumnSerializer;
import org.apache.cassandra.io.IColumnSerializer.Flag;

public class ExpiringColumn extends Column {

	private final int localExpirationTime;

	public ExpiringColumn(ByteBuffer name, ByteBuffer value, long timestamp,int timeToLive) {
		this(name, value, timestamp,timeToLive,(int)(System.currentTimeMillis()/1000)+timeToLive);
		
	}

	public ExpiringColumn(ByteBuffer name, ByteBuffer value, long timestamp,
			int timeToLive, int localExpirationTime) {
		super(name,value,timestamp);
		assert timeToLive>0:timeToLive;
		assert localExpirationTime>0:localExpirationTime;
		this.timeToLive=timeToLive;
		this.localExpirationTime=localExpirationTime;
	}

	private final int timeToLive;

	@Override
	public ByteBuffer name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IColumn> getSubColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int serializationFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public static IColumn create(ByteBuffer name, ByteBuffer value, long timestamp,
			int timeToLive, int localExpirationTime, int expireBefore, Flag flag) {
		if(localExpirationTime>=expireBefore||flag==IColumnSerializer.Flag.PRESERVE_SIZE){
			return new ExpiringColumn(name,value,timestamp,timeToLive,localExpirationTime);
		}
		return new DeletedColumn(name,localExpirationTime,timestamp);
	}

}
