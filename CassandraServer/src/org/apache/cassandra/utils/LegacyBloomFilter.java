package org.apache.cassandra.utils;

import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyBloomFilter extends Filter {

    private static final int EXCESS = 20;
    private static final Logger logger = LoggerFactory.getLogger(LegacyBloomFilter.class);
    static LegacyBloomFilterSerializer serializer_ = new LegacyBloomFilterSerializer();
	private BitSet fileter_;

    public LegacyBloomFilter(int hashes, BitSet bs) {
		hashCount=hashes;
		fileter_=bs;
	}

	public static LegacyBloomFilterSerializer serializer()
    {
        return serializer_;
    }

	public static Filter getFilter(long numElements, int targetBucketsPerElem) {
		int maxBucketsPerElement=Math.max(1,BloomCalculations.maxBucketsPerElement(numElements));
		int bucketsPerElement=Math.min(targetBucketsPerElem, maxBucketsPerElement);
		if(bucketsPerElement<targetBucketsPerElem){
			 logger.warn(String.format("Cannot provide an optimal LegacyBloomFilter for %d elements (%d/%d buckets per element).",
                     numElements, bucketsPerElement, targetBucketsPerElem));
		}
		BloomCalculations.BloomSpecification spec=BloomCalculations.computeBloomSpec(bucketsPerElement);
		return new LegacyBloomFilter(spec.K,bucketsFor(numElements,spec.bucketsPerElement));
	}

	private static BitSet bucketsFor(long numElements, int bucketsPer) {
		long numBits=numElements*bucketsPer+EXCESS;
		return new BitSet((int)Math.min(Integer.MAX_VALUE, numBits));
	}
}
