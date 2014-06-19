package org.apache.cassandra.dht;

public abstract class AbstractBounds<T> {

	public final T left;
	public final T right;
	protected transient final IPartitioner partitioner;
	
	public AbstractBounds(T left,T right,IPartitioner partitioner){
		this.left=left;
		this.right=right;
		this.partitioner=partitioner;
	}
}
