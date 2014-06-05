package org.apache.cassandra.db;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.Callable;

import org.github.jamm.MemoryMeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Memtable {
	private static final Logger logger = LoggerFactory.getLogger(Memtable.class);
	private ColumnFamilyStore cfs;
	private long creationTime;
	private MemoryMeter meter;
	
	public Memtable(ColumnFamilyStore cfStore) {
		this.cfs=cfStore;
		this.creationTime=System.currentTimeMillis();
		
		Callable<Set<Object>> provider=new Callable<Set<Object>>(){

			@Override
			public Set<Object> call() throws Exception {
				Set<Object> set=Collections.newSetFromMap(new IdentityHashMap<Object,Boolean>());
				set.add(Memtable.this.cfs.metadata);
				return set;
			}
			
		};
		meter=new MemoryMeter().omitSharedBufferOverhead().withTrackerProvider(provider);
	}

}
