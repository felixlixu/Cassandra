package org.apache.cassandra.dht;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.dht.Token.TokenFactory;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.GuidGenerator;
import org.apache.cassandra.utils.Pair;

public class RandomPartitioner extends AbstractPartitioner<BigIntegerToken> {

	public static final BigInteger ZERO=new BigInteger("0");
	public static final BigIntegerToken MINIMUM=new BigIntegerToken("-1");
	public static final BigInteger MAXIMUM=new BigInteger("2").pow(127);
	
	private static final byte DELIMITER_BYTE=":".getBytes()[0];
	
	@Override
	public DecoratedKey<BigIntegerToken> decorateKey(ByteBuffer key) {
		return new DecoratedKey<BigIntegerToken>(getToken(key),key);
	}

	@Override
	public Token midpoint(Token left, Token right) {
		BigInteger leftInteger=left.equals(MINIMUM)?ZERO:((BigIntegerToken)left).token;
		BigInteger rightInteger=right.equals(MINIMUM)?ZERO:((BigIntegerToken)right).token;
		Pair<BigInteger,Boolean> midpair=FBUtilities.midpoint(leftInteger,rightInteger,127);
		return new BigIntegerToken(midpair.left);
	}

	@Override
	public BigIntegerToken getMinimumToken() {
		
		return MINIMUM;
	}

	@Override
	public BigIntegerToken getToken(ByteBuffer key) {
		if(key.remaining()==0){
			return MINIMUM;
		}
		return new BigIntegerToken(FBUtilities.hashToBigIngeger(key));
	}

	@Override
	public BigIntegerToken getRandomToken() {
		BigInteger token=FBUtilities.hashToBigIngeger(GuidGenerator.guidAsBytes());
		if(token.signum()==-1){
			token=token.multiply(BigInteger.valueOf(-1L));
		}
		return new BigIntegerToken(token);
	}

	@Override
	public TokenFactory getTokenFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean preservesOrder() {
		return false;
	}

	@Override
	public Map<Token, Float> describeOwnership(List<Token> sortedTokens) {
		Map<Token,Float> ownerships=new HashMap<Token,Float>();
		Iterator i=sortedTokens.iterator();
		//0 case
		if(!i.hasNext()){throw new RuntimeException("No nodes present in the cluster. How did you call this?");}
		//1 case
		if(sortedTokens.size()==1){
			ownerships.put((Token)i.next(), new Float(1.0));
		}
		//n case
		else{
			final BigInteger ri=MAXIMUM;
			final BigDecimal r=new BigDecimal(ri);
			Token start=(Token)i.next();
			BigInteger ti=((BigIntegerToken)start).token;
			Token t;
			BigInteger tim1=ti;
			while(i.hasNext()){
				t=(Token)i.next();
				ti=((BigIntegerToken)t).token;
				// %age = ((T(i) - T(i-1) + R) % R) / R
				float x=new BigDecimal(ti.subtract(tim1).add(ri).mod(ri)).divide(r).floatValue();
				ownerships.put(t, x);
				tim1=ti;
			}
            float x = new BigDecimal(((BigIntegerToken)start).token.subtract(ti).add(ri).mod(ri)).divide(r).floatValue();
            ownerships.put(start, x);
		}
		return ownerships;
	}

	@Override
	public DecoratedKey converFromDiskFormat(ByteBuffer key) {
		// TODO Auto-generated method stub
		return null;
	}

}
