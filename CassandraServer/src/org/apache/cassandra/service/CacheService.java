package org.apache.cassandra.service;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.cassandra.cache.AutoSavingCache;
import org.apache.cassandra.cache.KeyCacheKey;
import org.apache.cassandra.cache.RowCacheKey;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.ColumnFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		// TODO Auto-generated method stub
		return null;
	}

	private AutoSavingCache<KeyCacheKey, Long> initKeyCache() {
		// TODO Auto-generated method stub
		return null;
	}
}