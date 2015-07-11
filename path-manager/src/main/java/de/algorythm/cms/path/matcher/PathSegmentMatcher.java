package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.PathRule;

public class PathSegmentMatcher<K, R> extends PathMatcher<K, R> {

	public PathSegmentMatcher(PathRule<K, R> rule, String segment, String[] paramNames, String patternPrefix, PathMatcher<K,R> defaultMatcher) {
		super(rule, false, true, rule != null, segment, paramNames, patternPrefix, defaultMatcher);
	}

	@Override
	protected boolean match(MatchState<K,R> state) {
		if (segment.equals(state.getCurrentSegment()))
			return matchedPositive(state);
		else
			return matchedNegative(state);
	}
}
