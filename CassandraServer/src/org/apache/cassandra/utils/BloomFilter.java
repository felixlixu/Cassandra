package org.apache.cassandra.utils;

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

}
