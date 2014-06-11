package org.apache.cassandra.utils;

import java.nio.ByteBuffer;

import org.apache.cassandra.io.sstable.SSTableMetadataSerializer;
import org.apache.cassandra.utils.obs.OpenBitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloomFilter extends Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(BloomFilter.class);
	private static int EXCESS = 20;
	public OpenBitSet bitset;
	 static BloomFilterSerializer serializer_ = new BloomFilterSerializer();

	public BloomFilter(int hashes, OpenBitSet bucketsFor) {
		hashCount=hashes;
		bitset=bucketsFor;
	}

	public static Filter emptyFilter() {
		return new BloomFilter(0, bucketsFor(0, 0));
	}

	private static OpenBitSet bucketsFor(long numElements, int bucketsPer) {
		return new OpenBitSet(numElements * bucketsPer + EXCESS);
	}

	public static BloomFilterSerializer serializer() {
		return serializer_;
	}
	
	// hash bytebuffer.
    private long[] getHashBuckets(ByteBuffer key)
    {
        return BloomFilter.getHashBuckets(key, hashCount, bitset.size());
    }
    
    static long[] getHashBuckets(ByteBuffer b, int hashCount, long max)
    {
        long[] result = new long[hashCount];
        long hash1 = MurmurHash.hash64(b, b.position(), b.remaining(), 0L);
        long hash2 = MurmurHash.hash64(b, b.position(), b.remaining(), hash1);
        for (int i = 0; i < hashCount; ++i)
        {
            result[i] = Math.abs((hash1 + (long)i * hash2) % max);
        }
        return result;
    }
    
    /**
    * @return A BloomFilter with the lowest practical false positive probability
    * for the given number of elements.
    */
    public static BloomFilter getFilter(long numElements, int targetBucketsPerElem)
    {
        int maxBucketsPerElement = Math.max(1, BloomCalculations.maxBucketsPerElement(numElements));
        int bucketsPerElement = Math.min(targetBucketsPerElem, maxBucketsPerElement);
        if (bucketsPerElement < targetBucketsPerElem)
        {
            logger.warn(String.format("Cannot provide an optimal BloomFilter for %d elements (%d/%d buckets per element).",
                                      numElements, bucketsPerElement, targetBucketsPerElem));
        }
        BloomCalculations.BloomSpecification spec = BloomCalculations.computeBloomSpec(bucketsPerElement);
        if (logger.isTraceEnabled())
            logger.trace("Creating bloom filter for {} elements and spec {}", numElements, spec);
        return new BloomFilter(spec.K, bucketsFor(numElements, spec.bucketsPerElement));
    }

    /**
    * @return The smallest BloomFilter that can provide the given false positive
    * probability rate for the given number of elements.
    *
    * Asserts that the given probability can be satisfied using this filter.
    */
    public static BloomFilter getFilter(long numElements, double maxFalsePosProbability)
    {
        assert maxFalsePosProbability <= 1.0 : "Invalid probability";
        int bucketsPerElement = BloomCalculations.maxBucketsPerElement(numElements);
        BloomCalculations.BloomSpecification spec = BloomCalculations.computeBloomSpec(bucketsPerElement, maxFalsePosProbability);
        return new BloomFilter(spec.K, bucketsFor(numElements, spec.bucketsPerElement));
    }
	
    public void add(ByteBuffer key){
    	for(long bucketIndex:getHashBuckets(key)){
    		bitset.set(bucketIndex);
    	}
    }
    
	public boolean isPresent(ByteBuffer key){
		for(long bucketIndex:getHashBuckets(key)){
			if(!bitset.get(bucketIndex));
				return false;
		}
		return true;
	}
	
	public long serializedSize(){
		return serializer_.serializedSize(this);
	}
	
	public void clear(){
		bitset.clear(0,bitset.size());
	}

}
