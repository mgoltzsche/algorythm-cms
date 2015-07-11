package de.algorythm.cms.url.matcher;

import de.algorythm.cms.url.PathRule;

public class EmptySegmentMatcher<K,R> extends PathMatcher<K,R> {

	static private final String EMPTY = "";
	
	private final boolean firstSegment;
	
	public EmptySegmentMatcher(PathRule<K, R> rule, String[] paramNames, String patternPrefix, PathMatcher<K,R> defaultMatcher) {
		super(rule, false, true, rule != null, EMPTY, paramNames, patternPrefix, defaultMatcher);
		this.firstSegment = patternPrefix.isEmpty();
	}
	
	@Override
	protected boolean match(MatchState<K,R> state) {
		if (state.getCurrentSegment().isEmpty())
			return matchedPositive(state);
		else
			return matchedNegative(state);
	}
	
	@Override
	protected void appendSlash(StringBuilder str) {
		if (!firstSegment)
			str.append('/');
	}
}
