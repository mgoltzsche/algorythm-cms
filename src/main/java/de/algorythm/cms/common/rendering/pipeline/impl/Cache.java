package de.algorythm.cms.common.rendering.pipeline.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cache<K, V> {

	static public interface IValueLoader<K, V> {
		V populate(K key);
	}
	
	static private interface IValueAccessStrategy<K,V> {
		V get(Value<K,V> value);
	}
	static private class ValueLoader<K,V> implements IValueAccessStrategy<K,V> {
		
		private final Lock lock = new ReentrantLock();
		private final IValueLoader<K, V> loader;
		private final IValueAccessStrategy<K, V> loadedStrategy;
		
		public ValueLoader(final IValueLoader<K, V> loader, final IValueAccessStrategy<K, V> loadedStrategy) {
			this.loader = loader;
			this.loadedStrategy = loadedStrategy;
		}
		
		@Override
		public V get(final Value<K,V> value) {
			lock.lock();
			
			if (value.value == null) {
				try {
					value.value = loader.populate(value.key);
					value.accessStrategy = loadedStrategy;
				} finally {
					lock.unlock();
				}
			}
			
			return value.value;
		}
	}
	static private class Value<K,V> {
		private final K key;
		private V value;
		private IValueAccessStrategy<K,V> accessStrategy;
		
		public Value(final K key, final IValueAccessStrategy<K,V> accessStrategy) {
			this.key = key;
			this.accessStrategy = accessStrategy;
		}
	}
	private final IValueAccessStrategy<K, V> LOADED = new IValueAccessStrategy<K, V>() {
		@Override
		public V get(Value<K, V> value) {
			return value.value;
		}
	};
	
	private final Map<K, Value<K,V>> cached = new HashMap<K, Value<K,V>>();
	
	public V get(K key, IValueLoader<K, V> loader) {
		Value<K,V> value;
		
		synchronized(cached) {
			value = cached.get(key);
			
			if (value == null) {
				value = new Value<K,V>(key, new ValueLoader<K,V>(loader, LOADED));
				cached.put(key, value);
			}
		}
		
		return value.accessStrategy.get(value);
	}
}