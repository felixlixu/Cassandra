package org.apache.cassandra.concurrent;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class DebuggableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

	public DebuggableScheduledThreadPoolExecutor(String threadPoolName) {
		this(1,threadPoolName,Thread.NORM_PRIORITY);
	}

	public DebuggableScheduledThreadPoolExecutor(int i, String threadPoolName,
			int normPriority) {
		super(i,new NamedThreadFactory(threadPoolName,normPriority));
	}

}
