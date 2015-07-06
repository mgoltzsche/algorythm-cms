package de.algorythm.cms.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.algorythm.cms.path.IPathManager;
import de.algorythm.cms.path.MatchResult;
import de.algorythm.cms.path.PathManagerBuilder;

public class TestUrlMatcher {

	private IPathManager<String, String> testee;
	
	@Test
	public void test() {
		testee = new PathManagerBuilder<String, String>("default-resource")
			.addRule("test", "/web/c/{param1}/test")
			.addRule("a", "/web/a/{param2}/a")
			.addRule("x", "/web/c/{param3}/x")
			.addRule("y", "/web/c/{param2}/y")
			.addRule("p", "/web/c/{param}")
			.addRule("b", "/web/b")
			.addRule("c", "/web/c")
			.addRule("web", "/web")
			.addRule("k", "/k")
			.addRule("ka", "/k/a")
			.addRule("kb", "/k/b")
			.addRule("welcome", "")
			.build();
		
		assertPositiveMatch("/web/c/ert/test", "test");
		assertPositiveMatch("/web/c/param", "p");
		assertPositiveMatch("/web/c/param/x", "x");
		assertPositiveMatch("/web/b", "b");
		assertPositiveMatch("/web/c", "c");
		assertPositiveMatch("/web", "web");
		assertPositiveMatch("/k", "k");
//		assertPositiveMatch("", "welcome"); // Doesn't work
		
		assertNegativeMatch("/web/c/ert/test/asdf", "/web/c/ert/test", "test");
		assertNegativeMatch("/x", "", null);
//		assertNegativeMatch("/", "", null);
	}
	
	private void assertPositiveMatch(String path, String expectedKey) {
		long start = System.currentTimeMillis();
		MatchResult<String,String> match = testee.match(path);
		System.out.println(String.format("Match time: %d ms", System.currentTimeMillis() - start));
		assertTrue(path + " should match exactly", match.isExactMatch());
		assertEquals("Should match key", expectedKey, match.getMatch().getKey());
		assertEquals("Path", path, match.getPath());
	}
	
	private void assertNegativeMatch(String path, String expectedPath, String expectedKey) {
		long start = System.currentTimeMillis();
		MatchResult<String,String> match = testee.match(path);
		System.out.println(String.format("Match time: %d ms", System.currentTimeMillis() - start));
		assertFalse("no exact match expected", match.isExactMatch());
		
		if (expectedKey == null)
			assertNull(match.getMatch());
		else
			assertEquals("Key", expectedKey, match.getMatch().getKey());
		
		assertEquals("Path", expectedPath, match.getPath());
	}
}