package org.apache.cassandra.concurrent;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

	private final String id;
	private final int priority;

	public NamedThreadFactory(String threadPoolName, int normPriority) {
		this.id=threadPoolName;
		this.priority=normPriority;
	}

	public NamedThreadFactory(String jmxName) {
		this(jmxName,Thread.NORM_PRIORITY);
	}

	@Override
	public Thread newThread(Runnable arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
