package org.apache.cassandra.db;

import org.apache.cassandra.db.filter.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollationController {

	private static Logger logger = LoggerFactory.getLogger(CollationController.class);
	private final ColumnFamilyStore cfs;
	private final boolean mutableColumns;
	private final QueryFilter filter;
	private final int gcBefore;
	
	private int sstablesIterated=0;
	
	public CollationController(ColumnFamilyStore columnFamilyStore,
			boolean mutableColumns, QueryFilter filter,int gcBefore) {
		this.cfs=columnFamilyStore;
		this.mutableColumns=mutableColumns;
		this.filter=filter;
		this.gcBefore=gcBefore;
	}

	public ColumnFamily getTopLevelColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSstablesIterated() {
		return sstablesIterated;
	}

}
