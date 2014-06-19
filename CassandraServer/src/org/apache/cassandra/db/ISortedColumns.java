package org.apache.cassandra.db;

import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.utils.Allocator;

public interface ISortedColumns extends IIterableColumns {

    public interface Factory
    {

		ISortedColumns create(AbstractType comparator, boolean reversedInsertOrder);
    	
    }

	boolean isEmpty();

	public void addColumn(IColumn column, Allocator allocator);
	
	public static class DeletionInfo{
		private final long markedForDeleteAt;
		private final int localDeletionTime;

		public DeletionInfo(){
			this(Long.MIN_VALUE,Integer.MIN_VALUE);
		}

		public DeletionInfo(long markedForDeleteAt, int localDeletionTime) {
			this.markedForDeleteAt=markedForDeleteAt;
			this.localDeletionTime=localDeletionTime;
		}
	}
}
