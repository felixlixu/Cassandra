package org.apache.cassandra.db;

import java.nio.ByteBuffer;
import java.util.Comparator;

import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.ByteBufferUtil;

public class DecoratedKey<T extends Token> extends RowPosition {

	private static final IPartitioner partitioner=StorageService.getPartitioner();
	
	public static final Comparator<DecoratedKey> comparator=new Comparator<DecoratedKey>(){
		public int compare(DecoratedKey o1,DecoratedKey o2){
			return o1.compareTo(o2);
		}
	};
	
	public final T token;
	public final ByteBuffer key;
	
	public DecoratedKey(T token,ByteBuffer key){
		assert token!=null&&key!=null&&key.remaining()>0;
		this.token=token;
		this.key=key;
	}
	
	@Override
	public int hashCode(){
		return key.hashCode();
	}
	
	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public boolean isMinimum(IPartitioner partitioner) {
		
		return false;
	}

	@Override
	public int compareTo(RowPosition pos) {
		if(this==pos){
			return 0;
		}
		
		if(!(pos instanceof DecoratedKey)){
			return -pos.compareTo(this);
		}
		
		DecoratedKey otherKey=(DecoratedKey)pos;
		int cmp=token.compareTo(otherKey.getToken());
		return cmp==0?ByteBufferUtil.compareUnsigned(key, otherKey.key):cmp;
	}

	@Override
	public boolean equals(Object obj){
		if(this==obj)
			return true;
		if(obj==null||this.getClass()!=obj.getClass()){
			return false;
		}
		DecoratedKey other=(DecoratedKey)obj;
		return ByteBufferUtil.compareUnsigned(key, other.key)==0;
	}
	
	@Override
	public RowPosition.Kind Kind(){
		return RowPosition.Kind.ROW_KEY;
	}

}
