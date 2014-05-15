package org.apache.cassandra.dht;

/**
 *	Interface representing a position on the ring.
 *  Both Token and DecoratedKey represent a position in the ring, a token being
 *  less precise than a DecoratedKey (a token is really a rang of keys)
 **/
public interface RingPosition<T> extends Comparable<T> {

	public Token getToken();
	public boolean isMinimum(IPartitioner partitioner);
}
