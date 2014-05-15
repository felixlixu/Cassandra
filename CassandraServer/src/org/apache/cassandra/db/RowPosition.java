package org.apache.cassandra.db;

import java.nio.ByteBuffer;

import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.dht.RingPosition;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.service.StorageService;

public abstract class RowPosition implements RingPosition<RowPosition> {

	public static enum Kind
	{
		ROW_KEY,MIN_BOUND,MAX_BOUND;
		private static final Kind[] allKinds=Kind.values();
		
		static Kind fromOrdinal(int ordinal){
			return allKinds[ordinal];
		}
	}
	
	//private static final RowPositionSerializer serializer=new RowPositionSerializer();
	
	public abstract Token getToken();
	public abstract Kind Kind();
	
	public static RowPosition forKey(ByteBuffer key,IPartitioner<Token> p){
		return key==null||key.remaining()==0?p.getMinimumToken().minKeyBound():p.decorateKey(key);
	}
	
	public boolean isMinimum(){
		return isMinimum(StorageService.getPartitioner());
	}
}
