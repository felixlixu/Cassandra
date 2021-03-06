package org.apache.cassandra.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.cassandra.db.DBConstants;
import org.apache.cassandra.io.ISerializer;
import org.apache.cassandra.utils.obs.OpenBitSet;

public class BloomFilterSerializer implements ISerializer<BloomFilter> {

	@Override
	public void serialize(BloomFilter bf, DataOutput dos) throws IOException {
        int bitLength = bf.bitset.getNumWords();
        int pageSize = bf.bitset.getPageSize();
        int pageCount = bf.bitset.getPageCount();

        dos.writeInt(bf.getHashCount());
        dos.writeInt(bitLength);

        for (int p = 0; p < pageCount; p++)
        {
            long[] bits = bf.bitset.getPage(p);
            for (int i = 0; i < pageSize && bitLength-- > 0; i++)
                dos.writeLong(bits[i]);
        }
	}

	@Override
	public BloomFilter deserialize(DataInput dis) throws IOException {
		 int hashes = dis.readInt();
	        long bitLength = dis.readInt();
	        OpenBitSet bs = new OpenBitSet(bitLength << 6);
	        int pageSize = bs.getPageSize();
	        int pageCount = bs.getPageCount();

	        for (int p = 0; p < pageCount; p++)
	        {
	            long[] bits = bs.getPage(p);
	            for (int i = 0; i < pageSize && bitLength-- > 0; i++)
	                bits[i] = dis.readLong();
	        }

	        return new BloomFilter(hashes, bs);
	}

	@Override
	public long serializedSize(BloomFilter bf) {
		return DBConstants.intSize // hash count
	               + DBConstants.intSize // length
	               + bf.bitset.getNumWords() * DBConstants.longSize; // buckets
	}

}
