package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.cassandra.io.IColumnSerializer;

public class Column implements IColumn {

	private final ByteBuffer name;
	private final ByteBuffer value;
	private final long timestamp;

	public Column(ByteBuffer name, ByteBuffer value, long timestamp) {
		assert name!=null;
		assert value!=null;
		assert name.remaining()<=IColumn.MAX_NAME_LENGTH;
		this.name=name;
		this.value=value;
		this.timestamp=timestamp;
		
	}

	@Override
	public int serializedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static IColumnSerializer serializer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addColumn(CounterUpdateColumn column) {
		// TODO Auto-generated method stub
		
	}

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

	@Override
	public int getLocalDeletionTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long timestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuffer value() {
		// TODO Auto-generated method stub
		return null;
	}

}
