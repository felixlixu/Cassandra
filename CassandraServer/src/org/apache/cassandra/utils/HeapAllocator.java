package org.apache.cassandra.utils;

import java.nio.ByteBuffer;

public final class HeapAllocator extends Allocator {

	public static final HeapAllocator instance=new HeapAllocator();
	
	private HeapAllocator(){}
	
	public ByteBuffer allocate(int size){
		return ByteBuffer.allocate(size);
	}
}
