package org.apache.cassandra.db;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

public class CounterUpdateColumn extends Column {

	public CounterUpdateColumn(ByteBuffer name, long value,
			long timestamp) {
		this(name,ByteBufferUtil.bytes(value),timestamp);
	}

	public CounterUpdateColumn(ByteBuffer name, ByteBuffer bytes, long timestamp) {
		super(name,bytes,timestamp);
	}

}