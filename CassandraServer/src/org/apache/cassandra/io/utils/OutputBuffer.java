package org.apache.cassandra.io.utils;

import java.io.IOException;
import java.io.OutputStream;

public class OutputBuffer extends FastByteArrayOutputStream {

	public OutputBuffer(){
		this(128);
	}
	
	public OutputBuffer(int size) {
		super(size);
	}

	public int getLength() {
		return count;
	}

	public byte[] getData() {
		return buf;
	}


}
