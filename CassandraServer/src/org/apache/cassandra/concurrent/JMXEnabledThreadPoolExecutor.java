package org.apache.cassandra.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class JMXEnabledThreadPoolExecutor extends DebuggableThreadPoolExecutor implements JMXEnabledThreadPoolExecutorMBean {

	public JMXEnabledThreadPoolExecutor(int corePoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue,
			NamedThreadFactory threadFactory, String jmxPath) {
		this(corePoolSize,corePoolSize,keepAliveTime,unit,workQueue,threadFactory,jmxPath);
	}

	public JMXEnabledThreadPoolExecutor(int corePoolSize, int maxPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue,
			NamedThreadFactory threadFactory, String jmxPath) {
		super(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

}
