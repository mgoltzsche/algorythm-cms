package de.algorythm.cms.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import de.algorythm.cms.path.PathRuleException;
import de.algorythm.cms.path.IMatchState;
import de.algorythm.cms.path.IPathMatchHandler;
import de.algorythm.cms.path.IPathMatcher;
import de.algorythm.cms.path.matcher.PathMatcherBuilder;

public class TestUrlMatcher implements IPathMatchHandler<String, String> {

	private IPathMatcher<String, String> testee;
	private String matchedPathPrefix, matchedPath, matchedKey, matchedResource;
	private boolean hasMatchedPrefix, hasMatchedNegative, hasMatchedPositive;
	private Map<String, String> matchedParams;
	
	static private class NullHandler implements IPathMatchHandler<String, String> {
		@Override
		public void matchedPrefix(IMatchState<String, String> state) {}
		@Override
		public void matchedPositive(IMatchState<String, String> state) {}
		@Override
		public void matchedNegative(String path) {}
	}
	
	static private class MatcherBuilder {
		private PathMatcherBuilder<String, String> testee = new PathMatcherBuilder<String, String>();
		public MatcherBuilder addRule(String key, String pattern) {
			testee.addRule(key, pattern, "resource-" + key);
			return this;
		}
		public IPathMatcher<String, String> build() throws PathRuleException {
			return testee.build();
		}
	}
	
	@Override
	public void matchedPrefix(final IMatchState<String, String> state) {
		hasMatchedPrefix = true;
		matched(state);
	}

	@Override
	public void matchedPositive(final IMatchState<String, String> state) {
		hasMatchedPositive = true;
		matched(state);
	}

	@Override
	public void matchedNegative(final String path) {
		hasMatchedNegative = true;
		matchedPath = path;
	}
	
	private void matched(IMatchState<String, String> state) {
		matchedPathPrefix = state.getPrefix();
		matchedPath = state.getPath();
		matchedKey = state.getKey();
		matchedResource = state.getResource();
		matchedParams = state.getParameters();
	}

	@Test
	public void match_should_match_pathes_correctly() throws PathRuleException {
		testee = new MatcherBuilder()
			.addRule("test", "/web/c/{param1}/test")
			.addRule("a", "/web/a/{param2}/a")
			.addRule("x", "/web/c/{paramx}/x")
			.addRule("y", "/web/c/{param}/y")
			.addRule("p", "/web/c/{param}")
			.addRule("s", "/web/c/special")
			.addRule("doc", "/doc/{docpath+}")
			.addRule("c", "/web/c")
			.addRule("b", "/web/b")
			.addRule("bd", "/web/b/")
			.addRule("bx", "/web/b/x")
			.addRule("bp", "/web/b/{param}")
			.addRule("web", "/web")
			.addRule("k", "/k")
			.addRule("kp", "/k/{param+}")
			.addRule("ka", "/k/a")
			.addRule("kb", "/k/b")
			.addRule("welcome", "")
			.build();
		System.out.println(testee);
		
		assertPositiveMatch("/k", "k");
		assertPositiveMatch("/web", "web");
		assertPositiveMatch("/web/b", "b");
		assertPositiveMatch("/web/b/", "bd");
		assertPositiveMatch("/web/b/x", "bx");
		assertPositiveMatch("/web/b/pvalue", "bp", "param", "pvalue");
		assertPositiveMatch("/web/c", "c");
		assertPositiveMatch("/web/c/pvalue", "p", "param", "pvalue");
		
		assertPositiveMatch("/web/c/valueX/x", "x", "paramx", "valueX");
		assertPositiveMatch("/web/c/ert/test", "test", "param1", "ert");
		
		assertPositiveMatch("/web/c/special", "s");
		
		assertPositiveMatch("/k/a", "ka");
		assertPositiveMatch("/k/kp", "kp", "param", "kp");
		assertPositiveMatch("/k//////", "kp", "param", "/////");
		
		assertPositiveMatch("/doc/my/doc/path", "doc", "docpath", "my/doc/path");
		assertPositiveMatch("/doc/my/doc/path/", "doc", "docpath", "my/doc/path/");
		assertPositiveMatch("/doc/my/doc%20path", "doc", "docpath", "my/doc%20path");
		
		assertPositiveMatch("/web/b/x%20x", "bp", "param", "x x");
		
		assertPositiveMatch("", "welcome");
		
		assertNegativeMatch("/k/a/x", "/k/a", "ka");
		assertNegativeMatch("/x", "", "welcome");
		assertNegativeMatch("/", "", "welcome");
	}
	
	@Test
	public void match_should_perform_with_many_rules() throws PathRuleException {
		final int children = 10, levels = 5;
		final long start = System.currentTimeMillis();
		final MatcherBuilder builder = new MatcherBuilder();
		final int rules = generatePathRules(builder, children, levels, 0, "");
		testee = builder.build();
		final double elapsed = (System.currentTimeMillis() - start) / 1000.0;
		System.out.println(String.format("%d path rules with a depth of %d and %d children per segment compiled in %.3fs", rules, levels, children, elapsed));
		
		matchPerf(children - 1, children, levels, "Worst case");
		matchPerf(children / 2, children, levels, "Avg case");
		matchPerf(0,               children, levels, "Best case");
	}
	
	private int generatePathRules(MatcherBuilder builder, int children, int maxDepth, int depth, String prefix) {
		if (maxDepth == depth)
			return 0;
		
		int rulesGenerated = 0;
		
		for (int i = 0; i < children; i++) {
			String nextPrefix = prefix + "/segment-" + i;
			
			if (depth == maxDepth - 1) {
				builder.addRule(nextPrefix, nextPrefix);
				rulesGenerated++;
			} else {
				rulesGenerated += generatePathRules(builder, children, maxDepth, depth + 1, nextPrefix);
			}
		}
		
		return rulesGenerated;
	}
	
	private final void matchPerf(int pos, int segPerLevel, int levels, String caseLabel) {
		String path = "";
		
		for (int i = 0; i < levels; i++) {
			path += "/segment" + i;
		}
		
		final NullHandler handler = new NullHandler();
		final long start = System.nanoTime();
		
		testee.match(path, handler);
		
		final double elapsed = (System.nanoTime() - start) / 1000000.0;
		
		System.out.println(String.format("%10s match took %.3fms", caseLabel, elapsed));
	}
	
	@Test
	public void build_should_detect_ambiguous_rules() {
		try {
			new MatcherBuilder()
			.addRule("a", "/web/a")
			.addRule("a", "/web/b")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
		
		try {
			new MatcherBuilder()
			.addRule("a", "/web/a")
			.addRule("b", "/web/a")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
		
		try {
			new MatcherBuilder()
			.addRule("a", "/web/{param+}/a")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
		
		try {
			new MatcherBuilder()
			.addRule("a", "/web/{param+}")
			.addRule("b", "/web/{param}")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
		
		try {
			new MatcherBuilder()
			.addRule("a", "/web/{param1}")
			.addRule("b", "/web/{param2+}")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
		
		try {
			new MatcherBuilder()
			.addRule("a", "/web/{param1}")
			.addRule("b", "/web/{param2}")
			.build();
			throw new AssertionError("Expected exception " + PathRuleException.class.getName());
		} catch(PathRuleException e) {}
	}
	
	private void assertPositiveMatch(String path, String expectedKey, String... params) {
		String expectedResource = "resource-" + expectedKey;
		hasMatchedPrefix = hasMatchedPositive = hasMatchedNegative = false;
		matchedKey = matchedResource = matchedPathPrefix = null;
		matchedParams = null;
		long start = System.nanoTime();
		testee.match(path, this);
		System.out.println(String.format("Match time: %.3f ms", (System.nanoTime() - start) / 1000000.0));
		assertTrue("Should match positive", hasMatchedPositive);
		assertFalse("Should not match negative", hasMatchedNegative);
		assertNotNull(path + " should match exactly", matchedResource);
		assertEquals("Should match key", expectedKey, matchedKey);
		assertEquals("Should match resource", expectedResource, matchedResource);
		assertEquals("Path", path, matchedPath);
		assertNotNull("Parameters should not be null", matchedParams);
		assertEquals("Parameter amount", params.length / 2, matchedParams.size());
		
		for (int i = 0; i < params.length; i += 2) {
			assertNotNull("Should match param " + params[i], matchedParams.get(params[i]));
			assertEquals("Should match param value", params[i + 1], matchedParams.get(params[i]));
		}
	}
	
	private void assertNegativeMatch(String path, String expectedPrefix, String expectedKey) {
		hasMatchedPrefix = hasMatchedPositive = hasMatchedNegative = false;
		matchedKey = matchedResource = matchedPathPrefix = null;
		matchedParams = null;
		long start = System.nanoTime();
		testee.match(path, this);
		System.out.println(String.format("Match time: %.3f ms", (System.nanoTime() - start) / 1000000.0));
		assertTrue("Should match prefix", hasMatchedPrefix);
		assertFalse("Should not match positive", hasMatchedPositive);
		assertTrue("Should match negative", hasMatchedNegative);
		
		if (expectedKey == null) {
			assertNull("no key expected", matchedKey);
			assertNull("no resource expected", matchedResource);
		} else {
			assertEquals("Should match key", expectedKey, matchedKey);
			assertEquals("Should match resource", "resource-" + expectedKey, matchedResource);
		}
		
		assertEquals("Path", path, matchedPath);
		assertEquals("Path prefix", expectedPrefix, matchedPathPrefix);
		assertNotNull("Parameters should not be null", matchedParams);
	}
}