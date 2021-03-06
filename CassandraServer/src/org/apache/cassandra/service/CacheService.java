package org.apache.cassandra.service;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.cassandra.cache.AutoSavingCache;
import org.apache.cassandra.cache.ICache;
import org.apache.cassandra.cache.KeyCacheKey;
import org.apache.cassandra.cache.RowCacheKey;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.ColumnFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//It is a single model.
public class CacheService implements CacheServiceMBean {

	public enum CacheType {
		KEY_CACHE("KeyCache"),
		ROW_CACHE("RowCache");
		
		private final String name;
		
		private CacheType(String typeName){
			name=typeName;
		}
		
		public String toString(){
			return name;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

	public static final String MBEAN_NAME = "org.apache.cassandra.db:type=Caches";
    public final AutoSavingCache<KeyCacheKey, Long> keyCache;
    public final AutoSavingCache<RowCacheKey, ColumnFamily> rowCache;

    private int rowCacheSavePeriod;
    private int keyCacheSavePeriod;

	private static final int AVERAGE_KEY_CACHE_ROW_SIZE=48;
	
	
	
	public final static CacheService instance = new CacheService();
	
	private CacheService(){
		MBeanServer mbs=ManagementFactory.getPlatformMBeanServer();
		try{
			mbs.registerMBean(this, new ObjectName(MBEAN_NAME));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
        rowCacheSavePeriod = DatabaseDescriptor.getRowCacheSavePeriod();
        keyCacheSavePeriod = DatabaseDescriptor.getKeyCacheSavePeriod();
        
        keyCache = initKeyCache();
        rowCache = initRowCache();
	}

	private AutoSavingCache<RowCacheKey, ColumnFamily> initRowCache() {
        logger.info("Initializing row cache with capacity of {} MBs and provider {}",
                DatabaseDescriptor.getRowCacheSizeInMB(),
                DatabaseDescriptor.getRowCacheProvider().getClass().getName());
        int rowCacheInMemoryCapacity=DatabaseDescriptor.getRowCacheSizeInMB()*1024*1204;
        ICache<RowCacheKey,ColumnFamily> rc=DatabaseDescriptor.getRowCacheProvider().create(rowCacheInMemoryCapacity, true);
        AutoSavingCache<RowCacheKey,ColumnFamily> rowCache=new AutoSavingCache<RowCacheKey,ColumnFamily>(rc,CacheType.ROW_CACHE);
        int rowCacheKeysToSave=DatabaseDescriptor.getRowCacheKeysToSave();
        logger.info("Scheduling row cache save to each {} seconds (going to save {} keys).",
                rowCacheSavePeriod,
                rowCacheKeysToSave == Integer.MAX_VALUE ? "all" : rowCacheKeysToSave);
        rowCache.scheduleSaving(rowCacheSavePeriod, rowCacheKeysToSave);
        return rowCache;
        
	}

	private AutoSavingCache<KeyCacheKey, Long> initKeyCache() {
		logger.info("Initializing key cache with capacity of {} MBs.", DatabaseDescriptor.getKeyCacheSizeInMB());
		int keyCacheInMemoryCapacity=DatabaseDescriptor.getKeyCacheSizeInMB()*1024*1204;
		ICache<KeyCacheKey,Long> kc=ConcurrentLinkedHashCache.Create(keyCacheInMemoryCapacity / AVERAGE_KEY_CACHE_ROW_SIZE);
		AutoSavingCache<KeyCacheKey, Long> keyCache = new AutoSavingCache<KeyCacheKey, Long>(kc, CacheType.KEY_CACHE);
		int keyCacheKeysToSave = DatabaseDescriptor.getKeyCacheKeysToSave();
		 logger.info("Scheduling key cache save to each {} seconds (going to save {} keys).",
                 keyCacheSavePeriod,
                 keyCacheKeysToSave == Integer.MAX_VALUE ? "all" : keyCacheKeysToSave);

     keyCache.scheduleSaving(keyCacheSavePeriod, keyCacheKeysToSave);

     return keyCache;
	}
}
