package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.utils.Allocator;

import edu.stanford.ppl.concurrent.SnapTreeMap;

public class AtomicSortedColumns implements ISortedColumns{

	private static  final Factory factory=new Factory(){

		@Override
		public ISortedColumns create(AbstractType comparator,
				boolean reversedInsertOrder) {
			return new AtomicSortedColumns(comparator);
		}
		
	};
	private AtomicReference<Holder> ref;

	public AtomicSortedColumns(AbstractType<?> comparator) {
		this(new Holder(comparator));
	}

	public AtomicSortedColumns(Holder holder) {
		this.ref=new AtomicReference<Holder>(holder);
	}

	@Override
	public Iterator<IColumn> iterator() {
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

	public static ISortedColumns.Factory factory() {
		return factory;
	}

	private static class Holder{

		private final SnapTreeMap<ByteBuffer, IColumn> map;

		public Holder(AbstractType<?> comparator) {
			this(new SnapTreeMap<ByteBuffer,IColumn>(comparator),new DeletionInfo());
		}

		public Holder(SnapTreeMap<ByteBuffer, IColumn> map,
				DeletionInfo deletionInfo) {
			this.map=map;
		}
		
	}
}
