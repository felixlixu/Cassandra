package org.apache.cassandra.dht;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.db.DecoratedKey;

//This is a interface partitioner for consistent hash.
public interface IPartitioner<T extends Token> {

	/**
	 * Transform key to object representation of the on-disk format. 
	 **/
	public DecoratedKey<T> decorateKey(ByteBuffer key);
	
	public Token midpoint(Token left,Token right);
	
	public T getMinimumToken();
	
	public T getToken(ByteBuffer key);
	
	public T getRandomToken();
	
	public Token.TokenFactory getTokenFactory();
	
	public boolean preservesOrder();
	
	public Map<Token,Float> describeOwnership(List<Token> sortedTokens);
	
	public <T extends RingPosition> T minValue(Class<T> klass);

}
