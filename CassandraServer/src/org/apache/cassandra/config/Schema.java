package org.apache.cassandra.config;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.db.Table;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.utils.Pair;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//Database has one schema and schema contains many tables, tables's stored  used Map.
public class Schema {
	
	private static final Logger logger = LoggerFactory.getLogger(Schema.class);
    
	public static final UUID INITIAL_VERSION = new UUID(4096, 0); // has type nibble set to 1, everything else to zero.

    public static final Schema instance = new Schema(INITIAL_VERSION);
    
    private static final int MIN_CF_ID=1000;
    private final AtomicInteger cfIdGen=new AtomicInteger(MIN_CF_ID);
    
    private volatile UUID version;
    
    /* metadata map for faster table lookup */
    private final Map<String,KSMetaData> tables=new NonBlockingHashMap<String,KSMetaData>();
    
    /* Table objects, one per keyspace. Only one instance should ever exist for any given keyspace. */
    private final Map<String, Table> tableInstances = new NonBlockingHashMap<String, Table>();
    
    
    private final BiMap<Pair<String, String>, Integer> cfIdMap = HashBiMap.create();
    
    public Schema(UUID initialVersion)
    {
        version = initialVersion;
    }

	public Set<String> getTables() {
		return tables.keySet();
	}

	/**
	 * Given a table name & column family name,get the column family metadata.
	 * If the table name or column family name is not valid this function returns null.
	 * */
	public CFMetaData getCFMetaData(String tablename, String cfName) {
		assert tablename!=null;
		KSMetaData ksm=tables.get(tablename);
		return (ksm==null)?null:ksm.cfMetaData().get(cfName);
	}

	public int nextCFId() {
		return cfIdGen.getAndIncrement();
	}

	public AbstractType getComparator(String table, String columnFamilyName) {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractType getSubComparator(String table, String columnFamilyName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Table getTableInstance(String tableName) {
		return tableInstances.get(tableName);
	}
	
	public void storeTableInstance(Table table){
		if(tableInstances.containsKey(table.name)){
			throw new IllegalArgumentException(String.format("Table %s was already initialized.", table.name));
		}
		tableInstances.put(table.name, table);
	}

	public KSMetaData getKSMetaData(String table) {
		assert table!=null;
		return tables.get(table);
	}

	public Pair<String, String> getCF(int cfId) {
		return cfIdMap.inverse().get(cfId);
	}

	public Map<String,CFMetaData> getTableMetaData(String table) {
		assert table!=null;
		KSMetaData ksm=tables.get(table);
		assert ksm!=null;
		return ksm.cfMetaData();
	}

	public KSMetaData getTableDefinition(String table) {
		return getKSMetaData(table);
	}

	public Integer getId(String ksName, String cfName) {
		return cfIdMap.get(new Pair<String,String>(ksName,cfName));
	}
}
