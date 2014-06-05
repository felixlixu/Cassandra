package org.apache.cassandra.db.index;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.cassandra.db.ColumnFamilyStore;

public class SecondaryIndexManager {

	private ColumnFamilyStore baseCfs;
	private ConcurrentSkipListMap<ByteBuffer, SecondaryIndex> indexesByColumn;
	private HashMap<Class<? extends SecondaryIndex>, SecondaryIndex> rowLevelIndexMap;

	public SecondaryIndexManager(ColumnFamilyStore baseCfs) {
		indexesByColumn=new ConcurrentSkipListMap<ByteBuffer,SecondaryIndex>();
		rowLevelIndexMap=new HashMap<Class<? extends SecondaryIndex>,SecondaryIndex>();
		this.baseCfs=baseCfs;
	}

}
