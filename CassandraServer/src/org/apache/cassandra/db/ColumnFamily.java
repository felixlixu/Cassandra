package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.marshal.AbstractType;

public class ColumnFamily extends AbstractColumnContainer {

	protected ColumnFamily(ISortedColumns columns) {
		super(columns);
	}

	private static ColumnFamilySerializer serializer=new ColumnFamilySerializer();
	
    public static AbstractType getComparatorFor(String table, String columnFamilyName, ByteBuffer superColumnName)
    {
        return superColumnName == null
               ? Schema.instance.getComparator(table, columnFamilyName)
               : Schema.instance.getSubComparator(table, columnFamilyName);
    }

	public static ColumnFamilySerializer serializer() {
		return serializer;
	}

	@Override
	public Iterator<IColumn> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}