package org.apache.cassandra.cache;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.Pair;

public interface CacheKey {

	public ByteBuffer serializeForStorage();
	
	public int serializedSize();
	
	public Pair<String,String> getPathInfo();
}
