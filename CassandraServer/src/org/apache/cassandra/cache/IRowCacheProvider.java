package org.apache.cassandra.cache;

import org.apache.cassandra.db.ColumnFamily;

public interface IRowCacheProvider {

	public ICache<RowCacheKey,ColumnFamily> create(int capacity,boolean useMemoryWeigher);
}
