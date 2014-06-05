package org.apache.cassandra.db;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Table = keyspace.
public class Table {

	private static final Logger logger = LoggerFactory.getLogger(Table.class);
	public static final String SYSTEM_TABLE = "system";
	public static final String SNAPSHOT_SUBDIR_NAME = "snapshots";
	public final String name;
	private volatile AbstractReplicationStrategy replicationStrategy;
	 /* ColumnFamilyStore per column family */
    private final Map<Integer, ColumnFamilyStore> columnFamilyStores = new ConcurrentHashMap<Integer, ColumnFamilyStore>();
    private final Object[] indexLocks;
    
	static{
		if(!StorageService.instance.isClientMode()){
			try{
				DatabaseDescriptor.createAllDirectories();
			}catch(IOException ex){
				throw new IOError(ex);
			}
		}
	}
	
	private Table(String table) {
		name=table;
		//get keyspace metadata
		KSMetaData ksm=Schema.instance.getKSMetaData(table);
		assert ksm!=null:"Unknown keyspace "+table;
		try{
			// create 
			createReplicationStrategy(ksm);
		}catch(ConfigurationException e){
			throw new RuntimeException(e);
		}
		indexLocks=new Object[DatabaseDescriptor.getConcurrentWriters()*128];
		for(int i=0;i<indexLocks.length;i++){
			indexLocks[i]=new Object();
		}
		for(String dataDir:DatabaseDescriptor.getAllDataFileLocations()){
			try{
				String keyspaceDir=dataDir+File.separator+table;
				if(!StorageService.instance.isClientMode()){
					FileUtils.createDirectory(keyspaceDir);
				}
				File streamDir=new File(keyspaceDir,"stream");
				if(streamDir.exists()){
					FileUtils.deleteRecursive(streamDir);
				}
			}catch(IOException ex){
				throw new IOError(ex);
			}
		}
		//cfMetaData= columnFumily meta data.
		for(CFMetaData cfm:new ArrayList<CFMetaData>(Schema.instance.getTableDefinition(table).cfMetaData().values())){
            logger.debug("Initializing {}.{}", name, cfm.cfName);
            initCf(cfm.cfId, cfm.cfName);
		}
	}

	//cfId is column family Id cfName equal column family name.
	public void initCf(Integer cfId, String cfName) {
		assert !columnFamilyStores.containsKey(cfId) : String.format("tried to init %s as %s, but already used by %s",
                cfName, cfId, columnFamilyStores.get(cfId));
		columnFamilyStores.put(cfId, ColumnFamilyStore.createColumnFamilyStore(this,cfName));
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

	//table=keyspace-->columnFamilyStore struct is ConcurrentHashMap-->superColumn->column->(Key,Value)
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

	public ColumnFamilyStore getColumnFamilyStore(String column_family) {
		return null;
	}

}
