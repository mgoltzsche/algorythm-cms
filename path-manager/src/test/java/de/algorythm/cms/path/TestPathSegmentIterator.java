package de.algorythm.cms.path;

import java.util.LinkedList;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestPathSegmentIterator {
	
	PathSegmentIterator testee;
	
	@Test
	public void should_iterate_over_segments() {
		assertSegments("", "");
		assertSegments("/", "", "");
		assertSegments("/web", "", "web");
		assertSegments("/web/", "", "web", "");
		assertSegments("/web/welcome", "", "web", "welcome");
		assertSegments("/web/welcome/page1", "", "web", "welcome", "page1");
		assertEquals("Last prefix", "/web/welcome", testee.getLastPrefix());
		assertSegments("asdf", "asdf");
		assertSegments("/web/c/{param}", "", "web", "c", "{param}");
	}
	
	private void assertSegments(String path, String... expectedSegments) {
		testee = new PathSegmentIterator(path);
		LinkedList<String> segments = new LinkedList<>();
		int i = 0;
		
		while (testee.hasNextSegment()) {
			String segment = testee.nextSegment();
			
			segments.add(segment);
			
			assertTrue("Should not return more segments, returned segments: " + segments, i < expectedSegments.length);
			assertEquals("Segment " + i + " of " + path, expectedSegments[i++], segment);
		}
		
		assertTrue("Should iterate over more segments, returned segments: " + segments, i == expectedSegments.length);
	}
}
