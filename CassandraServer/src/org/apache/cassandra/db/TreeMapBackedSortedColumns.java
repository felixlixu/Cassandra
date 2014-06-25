package org.apache.cassandra.db;

import java.util.Iterator;

import org.apache.cassandra.db.ISortedColumns.Factory;
import org.apache.cassandra.utils.Allocator;

public class TreeMapBackedSortedColumns extends AbstractThreadUnsafeSortedColumns implements ISortedColumns {

	@Override
	public Iterator<IColumn> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Factory factory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addColumn(IColumn column, Allocator allocator) {
		// TODO Auto-generated method stub
		
	}

}
