package org.apache.cassandra.utils;

import org.apache.cassandra.net.CallbackInfo;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class ExpiringMap<K, V> {

	private static final Logger logger = LoggerFactory.getLogger(ExpiringMap.class);
	
	private static class CacheableObject<T>{
		private final T value;
		private final long createdAt;
		private final long expiration;
		CacheableObject(T o,long e){
			assert o!=null;
			value=o;
			expiration=e;;
			createdAt=System.currentTimeMillis();
		}
		T getValue(){
			return value;
		}
		boolean isReadyToDieAt(long time){
			return ((time-createdAt)>expiration);
		}
	}
	
	private final NonBlockingHashMap<K, CacheableObject<V>> cache = new NonBlockingHashMap<K, CacheableObject<V>>();
	
	public ExpiringMap(long defaultCallbackTimeout,
			Function<Pair<K, V>, ?> timeoutReporter) {
		// TODO Auto-generated constructor stub
	}

	public V put(K key, V value,
			long timeout) {
		CacheableObject<V> previous=cache.put(key,new CacheableObject<V>(value,timeout));
		return (previous==null)?null:previous.getValue();
	}


}
