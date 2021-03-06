package org.apache.cassandra.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.cassandra.io.IColumnSerializer;
import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.ByteBufferUtil;

//Key value
public class Row {

	private static RowSerializer serializer=new RowSerializer();
	
	public static RowSerializer serializer(){
		return serializer;
	}
	
	public final DecoratedKey<?> key;
	public final ColumnFamily cf;
	
	public Row(DecoratedKey<?> key,ColumnFamily cf){
		assert key!=null;
		this.key=key;
		this.cf=cf;
	}
	
	public static class RowSerializer implements IVersionedSerializer<Row>{

		@Override
		public void serialize(Row row, DataOutput dos, int version)
				throws IOException {
			ByteBufferUtil.writeWithShortLength(row.key.key, dos);
			ColumnFamily.serializer().serialize(row.cf,dos);
		}
		
        public Row deserialize(DataInput dis, int version, IColumnSerializer.Flag flag, ISortedColumns.Factory factory) throws IOException
        {
            return new Row(StorageService.getPartitioner().decorateKey(ByteBufferUtil.readWithShortLength(dis)),
                           ColumnFamily.serializer().deserialize(dis, flag, factory));
        }

		@Override
		public Row deserialize(DataInput dis, int version) throws IOException {
			return deserialize(dis, version, IColumnSerializer.Flag.LOCAL, TreeMapBackedSortedColumns.factory());
		}

		@Override
		public long serializedSize(Row row, int version) {
			return DBConstants.shortSize + row.key.key.remaining() + ColumnFamily.serializer().serializedSize(row.cf);
		}
		
	}
}
