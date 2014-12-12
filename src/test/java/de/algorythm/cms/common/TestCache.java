package de.algorythm.cms.common;

import java.util.LinkedList;

import org.junit.Test;

import de.algorythm.cms.common.rendering.pipeline.impl.Cache;
import de.algorythm.cms.common.rendering.pipeline.impl.Cache.IValueLoader;
import static org.junit.Assert.*;

public class TestCache {

	private final LinkedList<String> populatedKeys = new LinkedList<String>();

	private class ValueLoader implements IValueLoader<String, String> {
		private String value;
		public ValueLoader(String value) {
			this.value = value;
		}
		@Override
		public String populate(String key) {
			populatedKeys.add(key);
			return value;
		}
	}

	@Test
	public void testCache() {
		Cache<String, String> testee = new Cache<String, String>();
		
		assertEquals("get synchronous", "aaa", testee.get("a", new ValueLoader("aaa")));
		assertEquals("populated keys size", 1, populatedKeys.size());
		assertEquals("populated key 0", "a", populatedKeys.get(0));
		assertEquals("get synchronous", "aaa", testee.get("a", new ValueLoader("aaa")));
		assertEquals("populated keys size after 2nd get() call", 1, populatedKeys.size());
	}
}
