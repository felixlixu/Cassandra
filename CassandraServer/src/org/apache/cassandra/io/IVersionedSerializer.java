package org.apache.cassandra.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.cassandra.db.ReadResponse;

public interface IVersionedSerializer<T> {

	public void serialize(T t,DataOutput dos,int version) throws IOException;
	
	public T deserialize(DataInput dis,int version) throws IOException;
	
	public long serializedSize(T t,int version);
}
