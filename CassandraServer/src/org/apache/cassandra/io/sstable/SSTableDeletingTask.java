package org.apache.cassandra.io.sstable;

import java.util.Set;

import org.apache.cassandra.db.DataTracker;
import org.apache.cassandra.utils.WrappedRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSTableDeletingTask extends WrappedRunnable {

	private static final Logger logger = LoggerFactory.getLogger(SSTableDeletingTask.class);
	private DataTracker tracker;
	private final Descriptor desc;
	private final Set<Component> components;
	private final long size;

	public SSTableDeletingTask(SSTableReader ssTableReader) {
		this.desc=ssTableReader.descriptor;
		this.components=ssTableReader.components;
		this.size=ssTableReader.bytesOnDisk();
		
	}

	@Override
	protected void runMayThrow() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setTracke(DataTracker tracker) {
		this.tracker=tracker;
	}

}
