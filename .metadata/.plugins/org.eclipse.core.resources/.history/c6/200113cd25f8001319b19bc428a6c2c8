package org.apache.cassandra.db;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.utils.Allocator;
import org.apache.cassandra.utils.HeapAllocator;

public abstract class AbstractColumnContainer implements IColumnContainer,IIterableColumns {

	protected final ISortedColumns columns;
	protected CFMetaData cfm;
	
	protected AbstractColumnContainer(ISortedColumns columns){
		this.columns=columns;
	}
	
	public boolean isEmpty(){
		return columns.isEmpty();
	}
	
	public void addColumn(IColumn column) {
		addColumn(column,HeapAllocator.instance);
	}

	public void addColumn(IColumn column, Allocator allocator) {
		columns.addColumn(column,allocator);
	}
}
