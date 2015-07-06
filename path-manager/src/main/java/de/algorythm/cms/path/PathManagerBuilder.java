package de.algorythm.cms.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.algorythm.cms.path.matcher.EmptySegmentMatcher;
import de.algorythm.cms.path.matcher.NegativeMatcher;
import de.algorythm.cms.path.matcher.PathParamMatcher;
import de.algorythm.cms.path.matcher.SegmentMatcher;
import de.algorythm.cms.path.matcher.SegmentParamMatcher;

public class PathManagerBuilder<K,R> {

	private final R defaultResource;
	
	public PathManagerBuilder(R defaultResource) {
		this.defaultResource = defaultResource;
	}
	
	private final List<PathRule<K, R>> rules = new LinkedList<>();
	
	public PathManagerBuilder<K, R> addRule(K key, String pattern) {
		rules.add(new PathRule<K, R>(key, pattern, null));
		return this;
	}
	
	public PathManagerBuilder<K, R> addRule(K key, String pattern, R resource) {
		rules.add(new PathRule<K, R>(key, pattern, resource));
		return this;
	}
	
	public IPathManager<K,R> build() {
		final Set<String> patternSet = new HashSet<>();
		final Set<K> keySet = new HashSet<>();
		final Map<String, Matcher<K,R>> matcherMap = new HashMap<>();
		final Matcher<K,R> negativeMatcher = new NegativeMatcher<>(defaultResource);
		final Matcher<K,R> rootMatcher = new EmptySegmentMatcher<>(true, null, "//", negativeMatcher);
		
		for (PathRule<K, R> rule : sortedByDepth(rules)) {
			if (!keySet.add(rule.getKey()))
				throw new IllegalStateException("Duplicate key " + rule.getKey());
			
			if (!patternSet.add(rule.getPattern()))
				throw new IllegalStateException("Duplicate pattern " + rule.getPattern());
			
			compile(rule, rootMatcher, negativeMatcher, matcherMap);
		}
		
		System.out.println(rootMatcher.getChild());
		
		return new PathManager<K, R>(rootMatcher.getChild());
	}
	
	private List<PathRule<K, R>> sortedByDepth(List<PathRule<K, R>> rules) {
		rules = new ArrayList<>(rules);
		
		Collections.sort(rules);
		
		return rules;
	}
	
	private void compile(PathRule<K, R> rule, Matcher<K,R> rootMatcher, Matcher<K,R> defaultMatcher, Map<String, Matcher<K,R>> matcherMap) {
		final PathSegmentIterator segmentIter = new PathSegmentIterator(rule.getPattern());
		final StringBuilder prefixBuilder = new StringBuilder();
		final List<String> paramNames = new LinkedList<>();
		Matcher<K,R> currentMatcher = rootMatcher;
		
		while (segmentIter.hasNextSegment()) {
			final String segment = segmentIter.nextSegment();
			final boolean finalSegment = !segmentIter.hasNextSegment();
			final PathRule<K, R> ruleFinal = finalSegment ? rule : null;
			
			Matcher<K,R> segmentMatcher = parseSegment(segment, ruleFinal, prefixBuilder, paramNames, defaultMatcher);
			final Matcher<K,R> existingMatcher = matcherMap.get(segmentMatcher.patternPrefix);
			
			if (existingMatcher == null) { // Add child if matcher does not yet exist
				matcherMap.put(segmentMatcher.patternPrefix, segmentMatcher);
				
				currentMatcher.addChild(segmentMatcher);
				currentMatcher = segmentMatcher;
			} else { // Use existing matcher as parent
				currentMatcher = existingMatcher;
				
				if (finalSegment && existingMatcher.finalMatcher)
					throw new IllegalStateException("Amiguous pattern " + existingMatcher.patternPrefix + " and " + segmentMatcher.patternPrefix + "\n\tDecision tree: " + rootMatcher.toString());
			}
		}
	}
	
	private Matcher<K,R> parseSegment(String segment, PathRule<K, R> rule, StringBuilder prefixBuilder, List<String> paramNames, Matcher<K, R> defaultMatcher) {
		if (segment.isEmpty()) {
			final boolean firstSegment = prefixBuilder.length() == 0;
			
			if (!firstSegment)
				prefixBuilder.append('/');
			
			return new EmptySegmentMatcher<K, R>(firstSegment, rule, prefixBuilder.toString(), defaultMatcher);
		} else if (segment.length() > 2 && segment.charAt(0) == '{' && segment.charAt(segment.length() - 1) == '}') {
			if (segment.length() > 3 && segment.charAt(segment.length() - 2) == '+') {
				// {param+}
				String paramName = segment.substring(1, segment.length() - 2);
				paramNames.add(paramName);
				return new PathParamMatcher<K, R>(rule, prefixBuilder.append("/*").toString(), defaultMatcher);
			} else {
				// {param}
				String paramName = segment.substring(1, segment.length() - 1);
				paramNames.add(paramName);
				return new SegmentParamMatcher<K,R>(rule, prefixBuilder.append("/*").toString(), defaultMatcher);
			}
		} else {
			// segment
			return new SegmentMatcher<K, R>(rule, segment,
					prefixBuilder.append('/').append(segment).toString(), defaultMatcher);
		}
	}
}
