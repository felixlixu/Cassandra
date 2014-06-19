package org.apache.cassandra.io.utils;

import java.io.IOException;
import java.io.OutputStream;

public class FastByteArrayOutputStream extends OutputStream {


	protected byte[] buf;
	protected int count;

	public FastByteArrayOutputStream(int size) {
		if(size>=0){
			buf=new byte[size];
		}else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
