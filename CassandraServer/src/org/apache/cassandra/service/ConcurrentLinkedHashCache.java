package org.apache.cassandra.service;

import java.util.Set;

import org.apache.cassandra.cache.ICache;
import org.apache.cassandra.cache.KeyCacheKey;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

public class ConcurrentLinkedHashCache<K,V> implements ICache<K,V> {

	private static int DEFAULT_CONCURENCY_LEVEL=64;

	@Override
	public int capcacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCapacity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(K key, V value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V get(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(K key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int weightedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<K> hotKeySet(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containKey(K key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPutCopying() {
		// TODO Auto-generated method stub
		return false;
	}

	public static <K, V> ConcurrentLinkedHashCache<K, V> Create(int capacity) {
		return create(capacity,Weighers.<V>singleton());
	}
	
	public static <K, V> ConcurrentLinkedHashCache<K, V> create(int weightedCapacity, com.googlecode.concurrentlinkedhashmap.Weigher<V> weigher){
		ConcurrentLinkedHashMap<K,V> map=new ConcurrentLinkedHashMap.Builder<K,V>()
				.weigher(weigher)
				.initialCapacity(0)
				.maximumWeightedCapacity(weightedCapacity)
				.concurrencyLevel(DEFAULT_CONCURENCY_LEVEL)
				.build();
		return new ConcurrentLinkedHashCache<K, V>();
	}

}
