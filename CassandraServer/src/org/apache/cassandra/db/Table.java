package org.apache.cassandra.db;

import java.io.IOError;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Table = keyspace.
public class Table {

	private static final Logger logger = LoggerFactory.getLogger(Table.class);
	public static final String SYSTEM_TABLE = "system";
	public final String name;
	private volatile AbstractReplicationStrategy replicationStrategy;
	 /* ColumnFamilyStore per column family */
    private final Map<Integer, ColumnFamilyStore> columnFamilyStores = new ConcurrentHashMap<Integer, ColumnFamilyStore>();
    
	static{
		if(!StorageService.instance.isClientMode()){
			try{
				DatabaseDescriptor.createAllDirectories();
			}catch(IOException ex){
				throw new IOError(ex);
			}
		}
	}
	
	public Table(String table) {
		name=table;
		KSMetaData ksm=Schema.instance.getKSMetaData(table);
		assert ksm!=null:"Unknown keyspace "+table;
		try{
			createReplicationStrategy(ksm);
		}catch(ConfigurationException e){
			throw new RuntimeException(e);
		}
	}

	public void createReplicationStrategy(KSMetaData ksm) throws ConfigurationException {
        if (replicationStrategy != null)
            StorageService.instance.getTokenMetadata().unregister(replicationStrategy);
            
        replicationStrategy = AbstractReplicationStrategy.createReplicationStrategy(ksm.name,
                                                                                    ksm.strategyClass,
                                                                                    StorageService.instance.getTokenMetadata(),
                                                                                    DatabaseDescriptor.getEndpointSnitch(),
                                                                                    ksm.strategyOptions);
	}

	public static Table open(String table) {
		return open(table,Schema.instance);
	}

	private static Table open(String table, Schema instance) {
		Table tableInstance=instance.getTableInstance(table);
		if(tableInstance==null){
			synchronized(Table.class){
				tableInstance=instance.getTableInstance(table);
				if(tableInstance==null){
					tableInstance=new Table(table);
					instance.storeTableInstance(tableInstance);
					
					for(ColumnFamilyStore cfs:tableInstance.getColumnFamilyStores())
						cfs.initRowCache();
				}
			}
		}
		return tableInstance;
	}

	private Collection<ColumnFamilyStore> getColumnFamilyStores() {
		return Collections.unmodifiableCollection(columnFamilyStores.values());
	}

	public AbstractReplicationStrategy getReplicationStrategy() {
		return this.replicationStrategy;
	}

}
