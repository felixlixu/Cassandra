package org.apache.cassandra.db.compaction;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.cassandra.cache.AutoSavingCache;
import org.apache.cassandra.cache.AutoSavingCache.Writer;
import org.apache.cassandra.concurrent.NamedThreadFactory;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.utils.WrappedRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompactionManager implements CompactionManagerMBean {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CompactionManager.class);
	
	private CompactionExecutor executor = new CompactionExecutor();
	
	public static final CompactionManager instance;
	
	static{
		instance=new CompactionManager();
	}

	public Future<?> submitCacheWrite(final Writer writer) {
		Runnable runnable=new WrappedRunnable(){
			public void runMayThrow() throws IOException{
				if(!AutoSavingCache.flushInProgress.compareAndSet(false,true)){
					logger.debug("Cache flushing was already in progress: skipping {}", writer.getCompactionInfo());
					return;
				}
				try{
					
				}finally{
					AutoSavingCache.flushInProgress.set(false);
				}
			}
		};
		return executor.submit(runnable);
	}
	
	public interface CompactionExecutorStatsCollector{
		
	}
	
	private static class CompactionExecutor extends ThreadPoolExecutor implements CompactionExecutorStatsCollector{

		public CompactionExecutor(int minThreads, int maxThreads, String name,BlockingQueue<Runnable> queue) {
			super(minThreads,maxThreads,60,TimeUnit.SECONDS,queue,new NamedThreadFactory(name,Thread.MAX_PRIORITY));
		}

		public CompactionExecutor(int threadCount,String name) {
			this(threadCount,threadCount,name, new LinkedBlockingQueue<Runnable>());
		}
		
		public CompactionExecutor(){
			this(Math.max(1, DatabaseDescriptor.getConcurrentCompactors()),"CompactionExecutor");
		}
		
	}

}
