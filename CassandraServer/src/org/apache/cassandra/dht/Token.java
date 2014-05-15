package org.apache.cassandra.dht;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.db.RowPosition;
import org.apache.cassandra.io.ISerializer;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.ByteBufferUtil;

public abstract class Token<T> implements RingPosition<Token<T>>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6652603025660589651L;
	private static final TokenSerializer serializer=new TokenSerializer();
	
	private static TokenSerializer serializer(){
		return serializer;
	}

	public final T token;
	
	private final transient KeyBound minimumBound=new KeyBound(true);
	private final transient KeyBound maximumBound=new KeyBound(false);
	
	protected Token(T token){
		this.token=token;
	}
	
    @Override
    public String toString()
    {
        return token.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        return token.equals(((Token<T>)obj).token);
    }

    @Override
    public int hashCode()
    {
        return token.hashCode();
    }
	
	abstract  public int compareTo(Token<T> o);
	
 	public static abstract class TokenFactory<T>{
		public abstract ByteBuffer toByteArray(Token<T> token);
		public abstract Token<T> fromByteArray(ByteBuffer bytes);
		public abstract String toString(Token<T> token);
		public abstract Token<T> fromString(String string);
		
		public abstract void validate(String token) throws ConfigurationException;
		
	}
	
	public class KeyBound extends RowPosition
	{

		public final boolean isMinimumBound;
		
		private KeyBound(boolean isMinimumBound){
			this.isMinimumBound=isMinimumBound;
		}
		
		@Override
		public Token getToken() {
			return Token.this;
		}

		@Override
		public boolean isMinimum(IPartitioner partitioner) {
			return getToken().isMinimum(partitioner);
		}

		@Override
		public int compareTo(RowPosition pos) {
			if(this==pos)
				return 0;
			int cmp=getToken().compareTo(pos.getClass());
			if(cmp!=0)
				return cmp;
			return isMinimumBound?-1:1;
		}
		
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null || this.getClass() != obj.getClass())
                return false;

            KeyBound other = (KeyBound)obj;
            return getToken().equals(other.getToken());
        }

        @Override
        public int hashCode()
        {
            return getToken().hashCode() + (isMinimumBound ? 0 : 1);
        }

        @Override
        public String toString()
        {
            return String.format("%s(%s)", isMinimumBound ? "min" : "max", getToken().toString());
        }

		@Override
		public Kind Kind() {
			return isMinimumBound?RowPosition.Kind.MIN_BOUND:RowPosition.Kind.MAX_BOUND;
		}
	}

	public static class TokenSerializer implements ISerializer<Token>{

		@Override
		public void serialize(Token token, DataOutput dos) throws IOException {
			IPartitioner p=StorageService.getPartitioner();
			ByteBuffer b=p.getTokenFactory().toByteArray(token);
			ByteBufferUtil.writeWithLength(b, dos);
		}

		@Override
		public Token deserialize(DataInput dis) throws IOException {
			IPartitioner p=StorageService.getPartitioner();
			int size=dis.readInt();
			byte[] bytes=new byte[size];
			dis.readFully(bytes);
			return p.getTokenFactory().fromByteArray(ByteBuffer.wrap(bytes));
		}

		@Override
		public long serializedSize(Token t) {
			throw new UnsupportedOperationException();
		}
		
	}

	public Token<T> getToken(){
		return this;
	}
	
	public boolean isMinimum(IPartitioner partitioner){
		return this.equals(partitioner.getMinimumToken());
	}
	
	public boolean isMinimum(){
		return isMinimum(StorageService.getPartitioner());
	}

	public KeyBound minKeyBound(IPartitioner partitioner){
		return minimumBound;
	}
	
	public KeyBound minKeyBound(){
		return minKeyBound(null);
	}
	
	public KeyBound maxKeyBound(IPartitioner partitioner){
		if(isMinimum(partitioner)){
			return minimumBound;
		}
		return maximumBound;
	}
	
	public KeyBound maxKeyBound(){
		return maxKeyBound(StorageService.getPartitioner());
	}

	public <T extends RingPosition> T asSplitValue(Class<T> klass){
		if(klass.equals(getClass())){
			return (T)this;
		}
		else{
			return (T)maxKeyBound();
		}
	}
}
