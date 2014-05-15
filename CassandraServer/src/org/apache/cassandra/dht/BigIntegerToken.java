package org.apache.cassandra.dht;

import java.math.BigInteger;

public class BigIntegerToken extends Token<BigInteger> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected BigIntegerToken(BigInteger token) {
		super(token);
	}

	public BigIntegerToken(String token){
		this(new BigInteger(token));
	}
	
	public int compareTo(Token<BigInteger> o){
		return token.compareTo(o.token);
	}
	
}
