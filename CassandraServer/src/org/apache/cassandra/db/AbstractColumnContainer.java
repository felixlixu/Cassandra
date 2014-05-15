package org.apache.cassandra.db;

public abstract class AbstractColumnContainer implements IColumnContainer,IIterableColumns {

	protected final ISortedColumns columns;
	
	protected AbstractColumnContainer(ISortedColumns columns){
		this.columns=columns;
	}
	
	public boolean isEmpty(){
		return columns.isEmpty();
	}
}
