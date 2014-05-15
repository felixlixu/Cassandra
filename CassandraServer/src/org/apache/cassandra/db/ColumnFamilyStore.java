package org.apache.cassandra.db;

import java.util.concurrent.TimeUnit;

import org.apache.cassandra.cache.AutoSavingCache;
import org.apache.cassandra.cache.RowCacheKey;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.filter.QueryFilter;
import org.apache.cassandra.db.filter.QueryPath;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.service.CacheService;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.DefaultInteger;
import org.apache.cassandra.utils.EstimatedHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnFamilyStore implements ColumnFamilyStoreMBean {

	private static Logger logger=LoggerFactory.getLogger(ColumnFamilyStore.class);
	
    // counts of sstables accessed by reads
    private final EstimatedHistogram recentSSTablesPerRead = new EstimatedHistogram(35);
    private final EstimatedHistogram sstablesPerRead = new EstimatedHistogram(35);
	
	static{
		StorageService.optionalTasks.scheduleWithFixedDelay(new MeteredFlusher(), 1000, 1000, TimeUnit.MILLISECONDS);
	}

	private Table table;
	private String columnFamily;
	private CFMetaData metadata;

	private DefaultInteger minCompactionThreshold;

	private DefaultInteger maxCompactionThreshold;

	private IPartitioner partitioner;
	
	public void initRowCache() {
		long start=System.currentTimeMillis();
		AutoSavingCache<RowCacheKey,ColumnFamily> rowCache=CacheService.instance.rowCache;
		
		int cacheRowRead=0;
		for(DecoratedKey key:rowCache.readSaved(table.name,columnFamily)){
			cacheRow(metadata.cfId,key);
		}
		
        if (cacheRowRead > 0)
            logger.info(String.format("completed loading (%d ms; %d keys) row cache for %s.%s",
                        System.currentTimeMillis() - start,
                        cacheRowRead,
                        table.name,
                        columnFamily));
	
	}
	
	private ColumnFamily cacheRow(Integer cfId, DecoratedKey decoratedKey) {
		RowCacheKey key=new RowCacheKey(cfId,decoratedKey);
		ColumnFamily cached;
		if((cached=CacheService.instance.rowCache.get(key))==null){
			cached =getTopLevelColumns(QueryFilter.getIdentityFilter(decoratedKey, new QueryPath(columnFamily)),
					Integer.MIN_VALUE,
					true);
			if(cached==null)
				return null;
			CacheService.instance.rowCache.put(key, cached);
		}
		return cached;
	}

	private ColumnFamily getTopLevelColumns(QueryFilter filter,
			int gcBefore, boolean forCache) {
		CollationController controller=new CollationController(this,forCache,filter,gcBefore);
		ColumnFamily columns=controller.getTopLevelColumns();
		recentSSTablesPerRead.add(controller.getSstablesIterated());
		sstablesPerRead.add(controller.getSstablesIterated());
		return columns;
	}

	private ColumnFamilyStore(Table table,String columnFamilyName,IPartitioner partitioner,int generation,CFMetaData metadata){
		assert metadata!=null: "null metadata for " + table + ":" + columnFamilyName;
		this.table=table;
		columnFamily=columnFamilyName;
		this.metadata=metadata;
		
		this.minCompactionThreshold=new DefaultInteger(metadata.getMinCompactionThreshold());
		this.maxCompactionThreshold=new DefaultInteger(metadata.getMaxCompactionThreshold());
		
		this.partitioner=partitioner;
	}

}
