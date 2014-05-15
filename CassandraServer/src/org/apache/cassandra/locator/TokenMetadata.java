package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.cassandra.dht.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TokenMetadata {

	private static Logger logger = LoggerFactory.getLogger(TokenMetadata.class);
	
	private BiMap<Token,InetAddress> tokenToEndpointMap;
	private final ReadWriteLock lock=new ReentrantReadWriteLock(true);
	private ArrayList<Token> sortedTokens;
	
	/* list of subscribers that are notified when the tokenToEndpointMap changed */
	 private final CopyOnWriteArrayList<AbstractReplicationStrategy> subscribes = new CopyOnWriteArrayList<AbstractReplicationStrategy>();

	 
		public TokenMetadata(HashBiMap<Token, InetAddress> create) {
			if(tokenToEndpointMap==null)
				tokenToEndpointMap=HashBiMap.create();
			this.tokenToEndpointMap=create;
			sortedTokens=sortTokens();
		}

		public TokenMetadata() {
			this(null);
		}

		private ArrayList<Token> sortTokens() {
			ArrayList<Token> tokens=new ArrayList<Token>(tokenToEndpointMap.keySet());
			Collections.sort(tokens);
			return tokens;
		}

		public void register(AbstractReplicationStrategy subscriber){
			subscribes.add(subscriber);
		}

		public ArrayList<Token> sortedTokens() {
			lock.readLock().lock();
			try{
				return sortedTokens;
			}
			finally{
				lock.readLock().unlock();
			}
		}

		public static Token firstToken(ArrayList<Token> ring,
				Token start) {
			return ring.get(firstTokenIndex(ring,start,false));
		}

		private static int firstTokenIndex(ArrayList ring, Token start,
				boolean insertMin) {
			assert ring.size()>0;
			int i=Collections.binarySearch(ring, start);
			if(i<0){
				i=(i+1)*(-1);
				if(i>=ring.size()){
					i=insertMin?-1:0;
				}
			}
			return i;
		}

		public TokenMetadata cloneOnlyTokenMap() {
			lock.readLock().lock();
			try{
				return new TokenMetadata(HashBiMap.create(tokenToEndpointMap));
			}
			finally{
				lock.readLock().unlock();
			}
		}

		public void unregister(AbstractReplicationStrategy replicationStrategy) {
			subscribes.remove(replicationStrategy);
		}
}