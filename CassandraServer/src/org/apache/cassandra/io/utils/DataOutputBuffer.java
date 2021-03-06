package org.apache.cassandra.io.utils;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class DataOutputBuffer extends DataOutputStream {

	public DataOutputBuffer(int size) {
		super(new OutputBuffer(size));
	}
	
	public OutputBuffer buffer(){
		return (OutputBuffer)out;
	}

	public int getLength() {
		return buffer().getLength();
	}

	public byte[] getData() {
		return buffer().getData();
	}

}
