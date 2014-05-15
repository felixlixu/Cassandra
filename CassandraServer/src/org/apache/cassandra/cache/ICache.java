package org.apache.cassandra.cache;

import java.util.Set;

/**
 *	This is similar to the Map interface,But requires maintaining a given capacity
 *	and does not require put or remove return values,which lets SerializingCache
 *	be more effecient by avoiding deserialize expect on get.
 **/
public interface ICache<K, V> {

	public int capcacity();
	
	public void setCapacity();
	
	public void put(K key,V value);
	
	public V get(K key);
	
	public void remove(K key);
	
	public int size();
	
	public int weightedSize();
	
	public void clear();
	
	public Set<K> keySet();
	
	public Set<K> hotKeySet(int n);
	
	public boolean containKey(K key);
	
	public boolean isPutCopying();
}
