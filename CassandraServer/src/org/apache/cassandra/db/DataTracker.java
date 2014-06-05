package org.apache.cassandra.db;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.cassandra.io.sstable.SSTableReader;
import org.apache.cassandra.utils.IntervalTree.IntervalTree;

public class DataTracker {

	private ColumnFamilyStore cfStore;
	private AtomicReference<View> view;

	public DataTracker(ColumnFamilyStore cfstore) {
		this.cfStore=cfstore;
		this.view=new AtomicReference<View>();
		this.init();
	}
	
	void init() {
		view.set(new View(new Memtable(cfStore),
				Collections.<Memtable>emptySet(),
				Collections.<SSTableReader>emptyList(),
				Collections.<SSTableReader>emptySet(),
				new IntervalTree()));
	}

	static class View{

		private final Memtable memtable;
		private final Set<Memtable> memtablePendingFlush;
		private final List<SSTableReader> sstables;
		private final Set<SSTableReader> compacting;
		private final IntervalTree intervalTree;

		public View(Memtable memtable, Set<Memtable> pendingFlush,
				List<SSTableReader> sstables, Set<SSTableReader> compacting,
				IntervalTree intervalTree) {
			this.memtable=memtable;
			this.memtablePendingFlush=pendingFlush;
			this.sstables=sstables;
			this.compacting=compacting;
			this.intervalTree=intervalTree;
		}
		
	}

}
