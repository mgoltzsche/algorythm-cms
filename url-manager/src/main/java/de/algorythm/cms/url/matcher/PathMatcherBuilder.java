package de.algorythm.cms.url.matcher;

import static de.algorythm.cms.url.RuleSegment.EMPTY_SEGMENT;
import static de.algorythm.cms.url.RuleSegment.PATH_PARAM;
import static de.algorythm.cms.url.RuleSegment.SEGMENT;
import static de.algorythm.cms.url.RuleSegment.SEGMENT_PARAM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.algorythm.cms.url.IPathMatcher;
import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathRuleException;
import de.algorythm.cms.url.PathSegmentIterator;
import de.algorythm.cms.url.RuleSegment;

public class PathMatcherBuilder<K,R> {

	private final List<PathRule<K, R>> rules = new LinkedList<>();
	
	public PathMatcherBuilder<K, R> addRule(PathRule<K, R> rule) {
		rules.add(rule);
		return this;
	}
	
	public PathMatcherBuilder<K, R> addRule(K key, String pattern, R resource) {
		rules.add(new PathRule<K, R>(key, pattern, resource));
		return this;
	}
	
	public IPathMatcher<K,R> build() throws PathRuleException {
		final Set<String> patternSet = new HashSet<>();
		final Set<K> keySet = new HashSet<>();
		final Map<String, PathMatcher<K,R>> matcherMap = new HashMap<>();
		final PathMatcher<K,R> negativeMatcher = new NegativeMatcher<>();
		final PathMatcher<K,R> rootMatcher = new EmptySegmentMatcher<>(null, null, "", negativeMatcher);
		
		for (PathRule<K, R> rule : sortedByDepth(rules)) {
			if (!keySet.add(rule.getKey()))
				throw new PathRuleException("Duplicate key " + rule.getKey());
			
			if (!patternSet.add(rule.getPattern()))
				throw new PathRuleException("Duplicate pattern " + rule.getPattern());
			
			compileRule(rule, rootMatcher, negativeMatcher, matcherMap);
		}
		
		return rootMatcher.getChild();
	}
	
	private List<PathRule<K, R>> sortedByDepth(List<PathRule<K, R>> rules) {
		rules = new ArrayList<>(rules);
		
		Collections.sort(rules);
		
		return rules;
	}
	
	private void compileRule(PathRule<K, R> rule, PathMatcher<K,R> rootMatcher, PathMatcher<K,R> defaultMatcher, Map<String, PathMatcher<K,R>> matcherMap) throws PathRuleException {
		final PathSegmentIterator segmentIter = new PathSegmentIterator(rule.getPattern());
		final StringBuilder prefixBuilder = new StringBuilder();
		final List<String> paramNames = new LinkedList<>();
		PathMatcher<K,R> currentMatcher = rootMatcher;
		
		while (segmentIter.hasNextSegment()) {
			final String segment = segmentIter.nextSegment();
			final boolean lastSegment = !segmentIter.hasNextSegment();
			final PathRule<K, R> ruleFinal = lastSegment ? rule : null;
			final PathMatcher<K,R> segmentMatcher = parseSegment(segment, ruleFinal, prefixBuilder, paramNames, defaultMatcher, lastSegment);
			final String patternPrefix = segmentMatcher.getPatternPrefix();
			final PathMatcher<K,R> existingMatcher = matcherMap.get(patternPrefix);
			
			if (existingMatcher == null) { // Add child if matcher does not yet exist
				matcherMap.put(patternPrefix, segmentMatcher);
				
				currentMatcher.addChild(segmentMatcher);
				currentMatcher = segmentMatcher;
			} else { // Use existing matcher as parent
				currentMatcher = existingMatcher;
				
				if (lastSegment && existingMatcher.isFinalMatcher())
					throw new PathRuleException("Amiguous pattern prefix " + existingMatcher.getPatternPrefix() + ". Decision tree: " + rootMatcher.toString());
			}
		}
	}
	
	private PathMatcher<K,R> parseSegment(String segment, PathRule<K, R> rule, StringBuilder prefixBuilder, List<String> paramNames, PathMatcher<K, R> defaultMatcher, boolean lastSegment) {
		String patternPrefix;
		String[] params;
		RuleSegment ruleSegment = RuleSegment.forSegment(segment);
		
		switch(ruleSegment.getType()) {
		case SEGMENT:
			patternPrefix = prefixBuilder.append('/').append(segment).toString();
			params = rule == null
					? null
					: paramNames.toArray(new String[paramNames.size()]);
			return new PathSegmentMatcher<K, R>(rule, segment, params, patternPrefix, defaultMatcher);
		case EMPTY_SEGMENT:
			if (prefixBuilder.length() > 0)
				prefixBuilder.append('/');
			patternPrefix = prefixBuilder.toString();
			params = copyParams(rule, paramNames);
			return new EmptySegmentMatcher<K, R>(rule, params, patternPrefix, defaultMatcher);
		case SEGMENT_PARAM:
			patternPrefix = prefixBuilder.append("/*").toString();
			paramNames.add(ruleSegment.getKey());
			params = copyParams(rule, paramNames);
			return new SegmentParamMatcher<K,R>(rule, params, patternPrefix, defaultMatcher);
		case PATH_PARAM:
			patternPrefix = prefixBuilder.append("/*").toString();
			paramNames.add(ruleSegment.getKey());
			params = copyParams(rule, paramNames);
			return new PathParamMatcher<K, R>(rule, params, patternPrefix, defaultMatcher);
		default:
			throw new IllegalStateException("Unsupported rule segment type");
		}
	}
	
	private String[] copyParams(PathRule<K, R> rule, List<String> paramNames) {
		return rule == null
			? null
			: paramNames.toArray(new String[paramNames.size()]);
	}
}
