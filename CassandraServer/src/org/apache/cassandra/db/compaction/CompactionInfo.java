package org.apache.cassandra.db.compaction;

import java.io.Serializable;

public class CompactionInfo implements Serializable {

	public static abstract class Holder {

	}

	private final int id;
	private final String ksname;
	private final String cfname;
	private final OperationType tasktype;
	private final long totalBytes;
	private final long bytesComplete;

	public CompactionInfo(int id, String ksname, String cfname,
			OperationType tasktype, long bytesWritten, long max) {
		this.id=id;
		this.ksname=ksname;
		this.cfname=cfname;
		this.tasktype=tasktype;
		this.bytesComplete=bytesWritten;
		this.totalBytes=max;
	}

	public CompactionInfo forProgress(long bytesWritten, long max) {
		return new CompactionInfo(id,ksname,cfname,tasktype,bytesWritten,max);
	}

}
