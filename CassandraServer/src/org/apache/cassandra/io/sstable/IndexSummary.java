package org.apache.cassandra.io.sstable;

import java.util.ArrayList;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.RowPosition;

public class IndexSummary {
	
	private long keysWritten=0;
	private ArrayList<KeyPosition> indexPositions;

	public IndexSummary(long expectedKeys) {
		long expectedExtries=expectedKeys/DatabaseDescriptor.getIndexInterval();
		if(expectedExtries>Integer.MAX_VALUE){
			throw new RuntimeException("Cannot use index_interval of"+DatabaseDescriptor.getIndexInterval()+" with "+expectedKeys+" (expected) keys");
		}
		indexPositions=new ArrayList<KeyPosition>((int)expectedExtries);
	}

	public boolean shouldAddEntry() {
		return keysWritten%DatabaseDescriptor.getIndexInterval()==0;
	}

	public void addEntry(DecoratedKey<?> decoratedKey, long indexPosition) {
		indexPositions.add(new KeyPosition(SSTable.getMinimalKey(decoratedKey),indexPosition));
	}

	public static final class KeyPosition implements Comparable<KeyPosition>
	{
		private final RowPosition key;
		private final long indexPosition;

		public KeyPosition(RowPosition key,long indexPosition){
			this.key=key;
			this.indexPosition=indexPosition;
		}

		@Override
		public int compareTo(KeyPosition arg0) {
			 return key.compareTo(arg0.key);
		}
		
		public String toString(){
			return key+":"+indexPosition;
		}
	}

	public void complete() {
		indexPositions.trimToSize();
	}

	public void incrementRowid() {
		keysWritten++;
	}
}
