package org.apache.cassandra.utils;

import java.nio.ByteBuffer;

public abstract class Allocator {

	public ByteBuffer clone(ByteBuffer buffer){
		assert buffer!=null;
		ByteBuffer cloned=allocate(buffer.remaining());
		
		cloned.mark();
		cloned.put(buffer.duplicate());
		cloned.reset();
		return cloned;
	}

	public abstract ByteBuffer allocate(int size);
}
			
