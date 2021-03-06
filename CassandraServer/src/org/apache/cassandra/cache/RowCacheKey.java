package org.apache.cassandra.cache;

import java.nio.ByteBuffer;

import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.Pair;

public class RowCacheKey implements CacheKey,Comparable<RowCacheKey> {

	
    public final int cfId;
    public final ByteBuffer key;

    public RowCacheKey(int cfId, DecoratedKey key)
    {
        this.cfId = cfId;
        this.key = key.key;
    }
	
	@Override
	public int compareTo(RowCacheKey otherKey) {
		return (cfId < otherKey.cfId) ? -1 : ((cfId == otherKey.cfId) ? ByteBufferUtil.compareUnsigned(key, otherKey.key) : 1);
	}

	@Override
	public ByteBuffer serializeForStorage() {
		ByteBuffer bytes=ByteBuffer.allocate(serializedSize());
		bytes.put(key.slice());
		bytes.rewind();
		return bytes;
	}

	@Override
	public int serializedSize() {
		return key.remaining();
	}

	@Override
	public Pair<String, String> getPathInfo() {
		return Schema.instance.getCF(cfId);
	}

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        RowCacheKey otherKey = (RowCacheKey) obj;

        return cfId == otherKey.cfId && key.equals(otherKey.key);
    }

    @Override
    public String toString()
    {
        return String.format("RowCacheKey(cfId:%d, key:%s)", cfId, key);
    }
}
