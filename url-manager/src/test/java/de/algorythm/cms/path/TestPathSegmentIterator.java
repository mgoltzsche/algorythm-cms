package de.algorythm.cms.path;

import java.util.LinkedList;

import org.junit.Test;

import de.algorythm.cms.url.PathSegmentIterator;
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
		assertSegments("asdf", "asdf");
		assertSegments("/web/c/{param}", "", "web", "c", "{param}");
		
		assertEquals("Last prefix", "/web/c/{param}", testee.getCurrentPrefix());
		
		testee = new PathSegmentIterator("/web/welcome/page1");
		testee.nextSegment();
		testee.nextSegment();
		testee.nextSegment();
		
		assertEquals("Current prefix", "/web/welcome", testee.getCurrentPrefix());
		assertEquals("Current prefix", "welcome/page1", testee.getCurrentSuffix());
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
