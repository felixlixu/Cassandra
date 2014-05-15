package org.apache.cassandra.io;

import java.io.DataInput;
import java.io.IOException;

import org.apache.cassandra.db.IColumn;

public interface IColumnSerializer extends ISerializer<IColumn> {
	public static enum Flag
	{
		LOCAL,FROM_REMOTE,PRESERVE_SIZE;
	}
	public IColumn deserializer(DataInput in,Flag flag,int expireBefore) throws IOException;
}
