package org.apache.cassandra.io.sstable;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.cassandra.cache.AutoSavingCache;
import org.apache.cassandra.cache.InstrumentingCache;
import org.apache.cassandra.cache.KeyCacheKey;
import org.apache.cassandra.concurrent.DebuggableThreadPoolExecutor;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.DataTracker;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.service.CacheService;
import org.apache.cassandra.utils.BloomFilter;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.FileUtils;
import org.apache.cassandra.utils.Filter;
import org.apache.cassandra.utils.LegacyBloomFilter;
import org.apache.cassandra.utils.RandomAccessReader;
import org.apache.cassandra.utils.SegmentedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSTableReaders are open()ed by Table.onStart; after that they are created by SSTableWriter.renameAndOpen
 *  Do not re-call open() on existing SSTable files; use the references kept by ColumnFamilyStore post-start instead.
 **/
public class SSTableReader extends SSTable {
	 private static final Logger logger = LoggerFactory.getLogger(SSTableReader.class);
	private SSTableMetadata sstableMetadata;
	private long maxDataAge;
	private SegmentedFile ifile;
	private SegmentedFile dfile;
	private IndexSummary indexSummary;
	private Filter bf;
	private SSTableDeletingTask deletingTask;
	private InstrumentingCache<KeyCacheKey, Long> keyCache;

	public SSTableReader(Descriptor descriptor, Set<Component> components,
			CFMetaData metadata, IPartitioner partitioner, SegmentedFile ifile,
			SegmentedFile dfile, IndexSummary indexSummary,
			Filter bloomFilter,
			long maxDataAge,
			SSTableMetadata sstableMetadata) {
		super(descriptor,components,metadata,partitioner);
		this.sstableMetadata=sstableMetadata;
		this.maxDataAge=maxDataAge;
		
		this.ifile=ifile;
		this.dfile=dfile;
		this.indexSummary=indexSummary;
		this.bf=bloomFilter;
		this.deletingTask=new SSTableDeletingTask(this);
	}

	public static Collection<SSTableReader> batchOpen(
			Set<Entry<Descriptor, Set<Component>>> entries,
			final Set<DecoratedKey> savedKeys, final DataTracker tracker, final CFMetaData metadata,
			final IPartitioner partitioner) {
		final Collection<SSTableReader> sstables=new LinkedBlockingQueue<SSTableReader>();
		
		ExecutorService executor=DebuggableThreadPoolExecutor.createWithPoolSize("SSTableBatchOpen", Runtime.getRuntime().availableProcessors());
		for(final Map.Entry<Descriptor, Set<Component>> entry:entries){
			Runnable runnable=new Runnable(){
				public void run(){
					SSTableReader sstable;
					try{
						sstable=open(entry.getKey(),entry.getValue(),savedKeys,tracker,metadata,partitioner);
					}catch(IOException ex){
						logger.error("Corrupt sstable " + entry + "; skipped", ex);
						return;
					}
					sstables.add(sstable);
				}
			};
			executor.submit(runnable);
		}
		executor.shutdown();
		try{
			executor.awaitTermination(7, TimeUnit.DAYS);
		}catch(InterruptedException e){
			throw new AssertionError(e);
		}
		return sstables;
	}

	protected static SSTableReader open(Descriptor descriptor, Set<Component> components,
			Set<DecoratedKey> savedKeys, DataTracker tracker,
			CFMetaData metadata, IPartitioner partitioner) throws IOException {
		assert partitioner!=null;
		assert components.contains(Component.DATA);
		assert components.contains(Component.PRIMARY_INDEX);
		
		long start=System.currentTimeMillis();
		logger.info("Opening {} ({} bytes)", descriptor, new File(descriptor.filenameFor(COMPONENT_DATA)).length());
		
		SSTableMetadata sstableMetadata=components.contains(Component.STATS)
				?SSTableMetadata.serializer.deserialize(descriptor)
				:SSTableMetadata.createDefaultInstance();
				
		String partitionerName=partitioner.getClass().getCanonicalName();
		if(partitionerName!=null&&!partitionerName.equals(sstableMetadata.partitioner)){
			throw new RuntimeException(String.format("Cannot open %s because partitioner does not match %s",
                    descriptor, partitionerName));
		}
		SSTableReader sstable=new SSTableReader(descriptor,
												components,
												metadata,
												partitioner,
												null,
												null,
												null,
												null,
												System.currentTimeMillis(),
												sstableMetadata);
		
		sstable.setTrackedBy(tracker);
		
		if(descriptor.hasStringInBloomFilter){
			sstable.load(true,savedKeys);
		}else{
			sstable.load(false,savedKeys);
			sstable.loadBloomFilter();
		}
        if (logger.isDebugEnabled())
            logger.debug("INDEX LOAD TIME for " + descriptor + ": " + (System.currentTimeMillis() - start) + " ms.");

        if (logger.isDebugEnabled() && sstable.getKeyCache() != null)
            logger.debug(String.format("key cache contains %s/%s keys", sstable.getKeyCache().size(), sstable.getKeyCache().getCapacity()));

        return sstable;
	}

	private void loadBloomFilter() throws IOException {
		if(!components.contains(Component.FILTER)){
			bf=BloomFilter.emptyFilter();
			return;
		}
		DataInputStream stream=null;
		try{
			stream=new DataInputStream(new BufferedInputStream(new FileInputStream(descriptor.filenameFor(Component.FILTER))));
			if(descriptor.usesOldBloomFilter){
				bf=LegacyBloomFilter.serializer().deserialize(stream);
			}else{
				bf=BloomFilter.serializer().deserialize(stream);
			}
		}
		finally{
			FileUtils.closeQuietly(stream);
		}
	}

	private void load(boolean recreatebloom, Set<DecoratedKey> keysToLoadInCache) throws IOException {
		boolean cacheLoading=keyCache!=null&&!keysToLoadInCache.isEmpty();
		
		SegmentedFile.Builder ibuilder=SegmentedFile.getBuilder(DatabaseDescriptor.getIndexAccessMode());
		SegmentedFile.Builder dbuilder=compression
									?SegmentedFile.getCompressedBuilder()
									:SegmentedFile.getBuilder(DatabaseDescriptor.getDiskAccessMode());
									
		RandomAccessReader input=RandomAccessReader.open(new File(descriptor.filenameFor(Component.PRIMARY_INDEX)),true);
		DecoratedKey left=null,right=null;
		try{
			long indexSize=input.length();
			long histogramCount=sstableMetadata.estimatedRowSize.count();
			long estimatedKeys=histogramCount>0&&!sstableMetadata.estimatedRowSize.isOverflowed()
							?histogramCount
							:SSTable.estimateRowFromIndex(input);
			indexSummary=new IndexSummary(estimatedKeys);
			if(recreatebloom)
				bf=LegacyBloomFilter.getFilter(estimatedKeys,15);
			while(true){
				long indexPosition=input.getFilePointer();
				if(indexPosition==indexSize)
					break;
				ByteBuffer key=null,skippedKey;
				skippedKey=ByteBufferUtil.readWithLength(input);
				
				boolean shouldAddEntry=indexSummary.shouldAddEntry();
				if(shouldAddEntry||cacheLoading||recreatebloom){
					key=skippedKey;
				}
				
				if(null==left)
					left=decodeKey(partitioner,descriptor,skippedKey);
				right=decodeKey(partitioner,descriptor,skippedKey);
				
				long dataPosition=input.readLong();
				if(key!=null){
					DecoratedKey decoratedKey=decodeKey(partitioner,descriptor,key);
					if(recreatebloom)
						bf.add(decoratedKey.key);
					if(shouldAddEntry)
						indexSummary.addEntry(decoratedKey,indexPosition);
					if(cacheLoading&&keysToLoadInCache.contains(decoratedKey))
						cacheKey(decoratedKey,dataPosition);
				}
				indexSummary.incrementRowid();
				ibuilder.addPotentialBoundary(indexPosition);
				dbuilder.addPotentialBoundary(dataPosition);
			}
			indexSummary.complete();
		}finally{
			FileUtils.closeQuietly(input);
		}
		
	}

	private DecoratedKey decodeKey(IPartitioner partitioner,
			Descriptor descriptor, ByteBuffer key) {
		if(descriptor.hasEncodedKeys)
			return partitioner.converFromDiskFormat(key);
		return partitioner.decorateKey(key);
	}

	private InstrumentingCache<KeyCacheKey,Long> getKeyCache() {
		return keyCache;
	}

	private void setTrackedBy(DataTracker tracker) {
		if(tracker!=null){
			keyCache=CacheService.instance.keyCache;
			deletingTask.setTracke(tracker);
		}
	}

	public long bytesOnDisk() {
		long bytes=0;
		for(Component component:components){
			bytes+=new File(descriptor.filenameFor(component)).length();
		}
		return bytes;
	}

}
