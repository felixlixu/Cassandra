package org.apache.cassandra.cache;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentingCache<K, V> {

	private final ICache<K,V> map;
	private final AtomicLong requests=new AtomicLong(0);
	private final AtomicLong hits=new AtomicLong(0);
	private volatile boolean capacitySetManually;
    private final AtomicLong lastRequests = new AtomicLong(0);
    private final AtomicLong lastHits = new AtomicLong(0);
	
	public InstrumentingCache(ICache<K, V> map) {
		this.map=map;
	}
	
	public void put(K key,V value){
		map.put(key, value);
	}
	
	public V get(K key){
		V v=map.get(key);
		requests.incrementAndGet();
		if(v!=null)
			hits.incrementAndGet();
		return v;
	}
	
	public V getInternal(K key){
		return map.get(key);
	}
	
	public void remove(K key){
		map.remove(key);
	}
	
	public int getCapacity(){
		return map.capcacity();
	}
	
	public boolean isCapacitySetManually(){
		return capacitySetManually;
	}
	
	public void updateCapacity(int capacity){
		map.setCapacity();
	}
	
	public void setCapacity(int capacity){
		updateCapacity(capacity);
		capacitySetManually=true;
	}
	
	public int size(){
		return map.size();
	}
	
	public int weightedSize(){
		return map.weightedSize();
	}
	
	public long getHits(){
		return hits.get();
	}
	
	public long getRequests(){
		return requests.get();
	}
	
	public double getRecentHitRate(){
        long r = requests.get();
        long h = hits.get();
        try
        {
            return ((double)(h - lastHits.get())) / (r - lastRequests.get());
        }
        finally
        {
            lastRequests.set(r);
            lastHits.set(h);
        }	
	}
	
    public void clear()
    {
        map.clear();
        requests.set(0);
        hits.set(0);
    }

    public Set<K> getKeySet()
    {
        return map.keySet();
    }

    public Set<K> hotKeySet(int n)
    {
        return map.hotKeySet(n);
    }

    public boolean containsKey(K key)
    {
        return map.containKey(key);
    }

    public boolean isPutCopying()
    {
        return map.isPutCopying();
    }

}
