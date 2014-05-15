package org.apache.cassandra.utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

public class EstimatedHistogram {

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
}
