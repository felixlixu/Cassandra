package org.apache.cassandra.utils;

import java.nio.ByteBuffer;

public abstract class Filter {

	int hashCount;
	
	int getHashCount(){
		return hashCount;
	}

	public abstract void add(ByteBuffer key);
}
