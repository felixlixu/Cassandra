package org.apache.cassandra.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

import org.apache.cassandra.io.ISerializer;

public class EstimatedHistogram {

	public static EstimatedHistogramSerializer serializer=new EstimatedHistogramSerializer();
	
	private long[] bucketOffsets;
	final AtomicLongArray buckets;
	
    public EstimatedHistogram()
    {
        this(90);
    }

    public EstimatedHistogram(int bucketCount)
    {
        makeOffsets(bucketCount);
        buckets = new AtomicLongArray(bucketOffsets.length + 1);
    }
    
    public EstimatedHistogram(long[] offsets, long[] bucketData)
    {
        assert bucketData.length == offsets.length +1;
        bucketOffsets = offsets;
        buckets = new AtomicLongArray(bucketData);
    }
	
    private void makeOffsets(int size) {
        bucketOffsets = new long[size];
        long last = 1;
        bucketOffsets[0] = last;
        for (int i = 1; i < size; i++)
        {
            long next = Math.round(last * 1.2);
            if (next == last)
                next++;
            bucketOffsets[i] = next;
            last = next;
        }
	}

	public void add(long n)
    {
        int index = Arrays.binarySearch(bucketOffsets, n);
        if (index < 0)
        {
            // inexact match, take the first bucket higher than n
            index = -index - 1;
        }
        // else exact match; we're good
        buckets.incrementAndGet(index);
    }
	
    public long[] getBucketOffsets()
    {
        return bucketOffsets;
    }
    
    public long[] getBuckets(boolean reset)
    {
        long[] rv = new long[buckets.length()];
        for (int i = 0; i < buckets.length(); i++)
            rv[i] = buckets.get(i);

        if (reset)
            for (int i = 0; i < buckets.length(); i++)
                buckets.set(i, 0L);

        return rv;
    }
	
    public static class EstimatedHistogramSerializer implements ISerializer<EstimatedHistogram>
    {
        public void serialize(EstimatedHistogram eh, DataOutput dos) throws IOException
        {
            long[] offsets = eh.getBucketOffsets();
            long[] buckets = eh.getBuckets(false);
            dos.writeInt(buckets.length);
            for (int i = 0; i < buckets.length; i++)
            {
                dos.writeLong(offsets[i == 0 ? 0 : i - 1]);
                dos.writeLong(buckets[i]);
            }
        }

        public EstimatedHistogram deserialize(DataInput dis) throws IOException
        {
            int size = dis.readInt();
            long[] offsets = new long[size - 1];
            long[] buckets = new long[size];

            for (int i = 0; i < size; i++) {
                offsets[i == 0 ? 0 : i - 1] = dis.readLong();
                buckets[i] = dis.readLong();
            }
            return new EstimatedHistogram(offsets, buckets);
        }

        public long serializedSize(EstimatedHistogram object)
        {
            throw new UnsupportedOperationException();
        }
    }

	public long count() {
	       long sum = 0L;
	       for (int i = 0; i < buckets.length(); i++) 
	           sum += buckets.get(i);
	       return sum;
	}

	public boolean isOverflowed() {
		return buckets.get(buckets.length()-1)>0;
	}
}
