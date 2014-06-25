package org.apache.cassandra.db;

import java.nio.ByteBuffer;

import org.apache.cassandra.io.IColumnSerializer;
import org.apache.cassandra.io.IColumnSerializer.Flag;

public class CounterColumn extends Column {

	private final long timestampOfLastDelete;

	public CounterColumn(ByteBuffer name, ByteBuffer value, long timestamp) {
		super(name, value, timestamp);
		this.timestampOfLastDelete=timestamp;
	}

	public long timestampOfLastDelete() {
		return timestampOfLastDelete;
	}

	public static IColumn create(ByteBuffer name, ByteBuffer value, long timestamp,
			long timestampOfLastDelete, Flag flag) {
		
		short count=value.getShort(value.position());
		if(flag==IColumnSerializer.Flag.FROM_REMOTE||(flag==IColumnSerializer.Flag.LOCAL&&count<0)){
			value=CounterContext.instance.clearAllDelta(value);
		}
		return new CounterColumn(name,value,timestamp,timestampOfLastDelete);
	}

}
