package org.apache.cassandra.cache;

import java.nio.ByteBuffer;


import org.apache.cassandra.io.sstable.Descriptor;
import org.apache.cassandra.utils.Pair;

public class KeyCacheKey extends Pair<Descriptor,ByteBuffer> implements CacheKey  {

	public KeyCacheKey(Descriptor desc,ByteBuffer key) {
		super(desc,key);
	}

	@Override
	public ByteBuffer serializeForStorage() {
		ByteBuffer bytes=ByteBuffer.allocate(serializedSize());
		bytes.put(right.slice());
		bytes.rewind();
		return bytes;
	}

	@Override
	public int serializedSize() {
		return right.remaining();
	}

	@Override
	public Pair<String, String> getPathInfo() {
		return new Pair<String,String>(left.ksname,left.cfname);
	}
	
    public String toString()
    {
        return String.format("KeyCacheKey(descriptor:%s, key:%s)", left, right);
    }

}
