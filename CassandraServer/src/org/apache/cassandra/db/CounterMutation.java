package org.apache.cassandra.db;

import java.nio.ByteBuffer;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class CounterMutation implements IMutation {

	public CounterMutation(RowMutation rm, ConsistencyLevel consistency_level) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	public ByteBuffer key() {
		// TODO Auto-generated method stub
		return null;
	}

}
