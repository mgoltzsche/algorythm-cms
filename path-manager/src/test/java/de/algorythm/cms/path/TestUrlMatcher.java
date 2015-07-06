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
			.addRule("x", "/web/c/{param}/x")
			.addRule("y", "/web/c/{param}/y")
			.addRule("p", "/web/c/{param}")
			.addRule("s", "/web/c/special")
			.addRule("c", "/web/c")
			.addRule("b", "/web/b")
			.addRule("bd", "/web/b/")
			.addRule("bp", "/web/b/{param}")
			.addRule("web", "/web")
			.addRule("k", "/k")
			.addRule("kp", "/k/{param+}")
			.addRule("ka", "/k/a")
			.addRule("kb", "/k/b")
			.addRule("welcome", "")
			.build();
		
		assertPositiveMatch("/k", "k");
		assertPositiveMatch("/web", "web");
		assertPositiveMatch("/web/b", "b");
		assertPositiveMatch("/web/b/", "bd");
		assertPositiveMatch("/web/b/param", "bp");
		assertPositiveMatch("/web/c", "c");
		assertPositiveMatch("/web/c/param", "p");
		assertPositiveMatch("/web/c/param/x", "x");
		assertPositiveMatch("/web/c/ert/test", "test");
		assertPositiveMatch("/web/c/special", "s");
		assertPositiveMatch("/k/a", "ka");
		assertPositiveMatch("/k/param", "kp");
		assertPositiveMatch("", "welcome");
		
		assertNegativeMatch("/web/c/ert/test/asdf", "/web/c/ert/test", "test");
		assertNegativeMatch("/x", "", "welcome");
		assertNegativeMatch("/", "", "welcome");
	}
	
	private void assertPositiveMatch(String path, String expectedKey) {
		long start = System.currentTimeMillis();
		MatchResult<String,String> match = testee.match(path);
		System.out.println(String.format("Match time: %d ms", System.currentTimeMillis() - start));
		assertTrue(path + " should match exactly", match != null && match.isExactMatch());
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