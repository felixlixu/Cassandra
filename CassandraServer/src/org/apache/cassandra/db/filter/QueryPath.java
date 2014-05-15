package org.apache.cassandra.db.filter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.cassandra.db.DBConstants;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.FBUtilities;

public class QueryPath {

	public final ByteBuffer superColumnName;
	public final ByteBuffer columnName;
	public final String columnFamilyName;
	
	public QueryPath(String columnFamilyName,ByteBuffer superColumnName,ByteBuffer columnName){
		this.columnFamilyName=columnFamilyName;
		this.columnName=columnName;
		this.superColumnName=superColumnName;
	}
	
	public QueryPath(String column_family, ByteBuffer byteBuffer) {
		this(column_family,byteBuffer,null);
	}

	public QueryPath(String columnFamily) {
		this(columnFamily,null);
	}
	
	public QueryPath(ColumnPath column_path){
		this(column_path.column_family,column_path.super_column,column_path.column);
	}
	
	public static QueryPath column(ByteBuffer columnName){
		return new QueryPath(null,null,columnName);
	}

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "(" +
               "columnFamilyName='" + columnFamilyName + '\'' +
               ", superColumnName='" + superColumnName + '\'' +
               ", columnName='" + columnName + '\'' +
               ')';
    }

    public void serialize(DataOutput dos) throws IOException
    {
        assert !"".equals(columnFamilyName);
        assert superColumnName == null || superColumnName.remaining() > 0;
        assert columnName == null || columnName.remaining() > 0;
        dos.writeUTF(columnFamilyName == null ? "" : columnFamilyName);
        ByteBufferUtil.writeWithShortLength(superColumnName == null ? ByteBufferUtil.EMPTY_BYTE_BUFFER : superColumnName, dos);
        ByteBufferUtil.writeWithShortLength(columnName == null ? ByteBufferUtil.EMPTY_BYTE_BUFFER : columnName, dos);
    }

    public static QueryPath deserialize(DataInput din) throws IOException
    {
        String cfName = din.readUTF();
        ByteBuffer scName = ByteBufferUtil.readWithShortLength(din);
        ByteBuffer cName = ByteBufferUtil.readWithShortLength(din);
        return new QueryPath(cfName.isEmpty() ? null : cfName, 
                             scName.remaining() == 0 ? null : scName, 
                             cName.remaining() == 0 ? null : cName);
    }

    public int serializedSize()
    {
        int size = DBConstants.shortSize + (columnFamilyName == null ? 0 : FBUtilities.encodedUTF8Length(columnFamilyName));
        size += DBConstants.shortSize + (superColumnName == null ? 0 : superColumnName.remaining());
        size += DBConstants.shortSize + (columnName == null ? 0 : columnName.remaining());
        return size;
    }
}
