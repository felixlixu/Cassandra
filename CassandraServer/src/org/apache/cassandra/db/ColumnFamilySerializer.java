package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.cassandra.db.ISortedColumns.Factory;
import org.apache.cassandra.io.IColumnSerializer.Flag;
import org.apache.cassandra.io.ISerializer;


public class ColumnFamilySerializer implements ISerializer<ColumnFamily> {

	
	
	@Override
	public void serialize(ColumnFamily t, DataOutput dos) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ColumnFamily deserialize(DataInput dis) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long serializedSize(ColumnFamily t) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ColumnFamily deserialize(DataInput dis, Flag flag, Factory factory) {
		// TODO Auto-generated method stub
		return null;
	}

}
