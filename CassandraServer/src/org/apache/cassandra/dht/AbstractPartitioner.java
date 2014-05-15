package org.apache.cassandra.dht;

public abstract class AbstractPartitioner<T extends Token> implements IPartitioner<T> {

	@Override
	public <R extends RingPosition> R minValue(Class<R> klass){
		Token minToken=getMinimumToken();
		if(minToken.getClass().equals(klass)){
			return (R)minToken;
		}else{
			return (R)minToken.minKeyBound();
		}
	}
}
