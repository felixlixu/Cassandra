package org.apache.cassandra.io.sstable;

import org.apache.cassandra.config.DatabaseDescriptor;

public class IndexSummary {
	
	private long keysWritten=0;

	public IndexSummary(long estimatedKeys) {
		// TODO Auto-generated constructor stub
	}

	public boolean shouldAddEntry() {
		return keysWritten%DatabaseDescriptor.getIndexInterval()==0;
	}

}
