package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class EmptySegmentMatcher<K,R> extends Matcher<K,R> {
	
	static private final String EMPTY = "";
	
	private final boolean firstSegment;
	
	public EmptySegmentMatcher(boolean firstSegment, PathRule<K, R> rule, String patternPrefix, Matcher<K,R> defaultMatcher) {
		super(rule, false, rule != null, true, EMPTY, patternPrefix, defaultMatcher);
		this.firstSegment = firstSegment;
	}
	
	@Override
	public boolean match(UrlMatchingState<K,R> state) {
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
