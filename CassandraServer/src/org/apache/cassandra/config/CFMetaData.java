package org.apache.cassandra.config;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.db.ColumnFamilyType;
import org.apache.cassandra.db.SystemTable;
import org.apache.cassandra.db.Table;
import org.apache.cassandra.db.compaction.AbstractCompactionStrategy;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.BytesType;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.cassandra.io.compress.CompressionParameters;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//ColumnFamily MetaData.
public final class CFMetaData {
	
	public static final double DEFAULT_READ_PEPAIR_CHANCE = 0.1;
	public static final boolean DEFAULT_REPLICATE_ON_WRITE=true;
	public static final int DEFAULT_GC_GRACE_SECONDS=864000;
	public static final int DEFAULT_MIN_COMPACTION_THRESHOLD=4;
	public static final int DEFAULT_MAX_COMPACTION_THRESHOLD=32;
	public static final double DEFAULT_MERGE_SHARDS_CHANCE=0.1;
	private static final String DEFAULT_COMPACTION_STRATEGY_CLASS="SizeTieredCompactionStrategy";
	

	private static Logger logger=LoggerFactory.getLogger(CFMetaData.class);
	
	public final ColumnFamilyType cfType;

	private String ksName;

	public final String cfName;

	private AbstractType comparator;

	public Integer cfId;

	private AbstractType subcolumnComparator;

	private double readRepairChance;			//default 1.0 (always),chance[0.0,1.0] of read repair
	private boolean replicateOnWrite;			//default false;
	private int gcGraceSeconds;                 // default 864000 (ten days)
	private int minCompactionThreshold;
	private int maxCompactionThreshold;
	private double mergeShardsChance;
	private BytesType defaultValidator;
	private AbstractType keyValidator;
	private String comment;
	private Object keyAlias;
	private Map<ByteBuffer, ColumnDefinition> column_metadata;
	private HashMap<String, String> compactionStrategyOptions;
	private CompressionParameters compressionParameters;
	private Class<? extends AbstractCompactionStrategy> compactionStrategyClass;
	
	public CFMetaData keyAlias(ByteBuffer prop){keyAlias=prop;return this;}
	public CFMetaData keyValidator(AbstractType prop){keyValidator=prop;return this;}
	public CFMetaData columnMetadata(Map<ByteBuffer,ColumnDefinition> prop){column_metadata=prop;return this;}
	public CFMetaData comment(String prop) { comment = enforceCommentNotNull(prop); return this;}
	public CFMetaData readRepairChance(double prop) {readRepairChance = prop; return this;}
	public CFMetaData gcGraceSeconds(int prop) {gcGraceSeconds = prop; return this;}
	public CFMetaData mergeShardsChance(double prop) {mergeShardsChance = prop; return this;}
	
	//
	public static final CFMetaData VersionCf=newSystemMetadata(SystemTable.VERSION_CF,7,"server version infomation",UTF8Type.instance,null);
	
	//Create System CF metadata and then set property.
	static{
		VersionCf.keyAlias(ByteBufferUtil.bytes("component"))
			.keyValidator(UTF8Type.instance)
			.columnMetadata(Collections.singletonMap(ByteBufferUtil.bytes("version"), 
					new ColumnDefinition(ByteBufferUtil.bytes("version"),
							UTF8Type.instance,
							null,
							null,
							null)));
	}
	
	public CFMetaData(String keyspace,String name,ColumnFamilyType type,AbstractType comp,AbstractType subcc){
		this(keyspace,name,type,comp,subcc,Schema.instance.nextCFId());
	}

	private static CFMetaData newSystemMetadata(String cfName, int cfId,
			String comment, UTF8Type comparator, AbstractType subcc) {
		ColumnFamilyType type=subcc==null?ColumnFamilyType.Standard:ColumnFamilyType.Super;
		CFMetaData newCFMD=new CFMetaData(Table.SYSTEM_TABLE,cfName,type,comparator,subcc,cfId);
        return newCFMD.comment(comment)
                .readRepairChance(0)
                .gcGraceSeconds(0)
                .mergeShardsChance(0.0);
		
	}


	private CFMetaData(String keyspace, String name, ColumnFamilyType type,
			AbstractType comp, AbstractType subcc, int id) {
		ksName=keyspace;
		cfName=name;
		cfType=type;
		comparator=comp;
		subcolumnComparator=enforceSubccDefault(type,subcc);
		
		cfId=id;
		this.init();
	}

	private AbstractType enforceSubccDefault(ColumnFamilyType type, AbstractType subcc) {
		return (subcc==null)&&(type==ColumnFamilyType.Super)?BytesType.instance:subcc;
	}

	private void init() {
		readRepairChance=DEFAULT_READ_PEPAIR_CHANCE;
		replicateOnWrite=DEFAULT_REPLICATE_ON_WRITE;
		gcGraceSeconds=DEFAULT_GC_GRACE_SECONDS;
		minCompactionThreshold=DEFAULT_MIN_COMPACTION_THRESHOLD;
		maxCompactionThreshold=DEFAULT_MAX_COMPACTION_THRESHOLD;
		mergeShardsChance=DEFAULT_MERGE_SHARDS_CHANCE;
		
		defaultValidator=BytesType.instance;
		keyValidator=BytesType.instance;
		comment="";
		keyAlias=null;
		column_metadata=new HashMap<ByteBuffer,ColumnDefinition>();
		
		try{
			compactionStrategyClass=createCompactionStrategy(DEFAULT_COMPACTION_STRATEGY_CLASS);
		}
		catch(ConfigurationException e){
			throw new AssertionError(e);
		}
		compactionStrategyOptions=new HashMap<String,String>();
		
		compressionParameters=new CompressionParameters(null);
		
	}

	private static Class<? extends AbstractCompactionStrategy> createCompactionStrategy(
			String className) throws ConfigurationException {
		try{
			return (Class<? extends AbstractCompactionStrategy>) Class.forName(className);
		}catch(Exception e){
			throw new ConfigurationException("Could not create Compaction Strategy of type " + className, e);
		}
	}

	public AbstractType getComparatorFor(ByteBuffer super_column) {
		return super_column==null?comparator:subcolumnComparator;
	}

	public AbstractType getKeyValidator() {
		return keyValidator;
	}
    private static String enforceCommentNotNull (CharSequence comment)
    {
        return (comment == null) ? "" : comment.toString();
    }
	public int getMinCompactionThreshold() {
		return minCompactionThreshold;
	}
	public int getMaxCompactionThreshold() {
		return maxCompactionThreshold;
	}
	public double getReadRepairChance() {
		return readRepairChance;
	}
	public AbstractType getDefaultValidator() {
		return defaultValidator;
	}
	public boolean getReplicateOnWrite() {
		return replicateOnWrite;
	}

}
