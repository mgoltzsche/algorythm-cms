package de.algorythm.cms.common.scheduling.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Function;

public class SynchronizedContext<K,V> {

	private final Map<K, Lock> lockMap = new HashMap<>(); 
	
	public V synchronize(final K key, final Function<K, V> fn) {
		Lock keyLock;
		boolean lockAdded = false;
		
		synchronized(lockMap) {
			keyLock = lockMap.get(key);
			
			if (keyLock == null) {
				keyLock = new ReentrantLock();
				lockMap.put(key, keyLock);
				lockAdded = true;
			}
		}
		
		keyLock.lock();
		
		try {
			return fn.apply(key);
		} finally {
			keyLock.unlock();
			
			if (lockAdded) {
				synchronized(lockMap) {
					lockMap.remove(key);
				}
			}
		}
	}
}
