package org.apache.cassandra.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface ISerializer<T> {

	public void serialize(T t,DataOutput dos) throws IOException;
	
	public T deserialize(DataInput dis) throws IOException;
	
	public long serializedSize(T t);
}
