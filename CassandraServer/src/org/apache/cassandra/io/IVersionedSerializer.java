package org.apache.cassandra.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IVersionedSerializer<T> {

	public void serialize(T t,DataOutput dos,int version) throws IOException;
	
	public T descrialize(DataInput dis,int version) throws IOException;
	
	public long serializedSize(T t,int version);
}
