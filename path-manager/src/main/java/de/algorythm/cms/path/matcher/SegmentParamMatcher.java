package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class SegmentParamMatcher<K,R> extends Matcher<K,R> {
	public SegmentParamMatcher(PathRule<K, R> rule, String patternPrefix, Matcher<K,R> defaultMatcher) {
		super(rule, false, rule != null, false, "*", patternPrefix, defaultMatcher);
	}
	@Override
	public boolean match(UrlMatchingState<K, R> state) {
		state.addParameterValue(state.getCurrentSegment());
		return matchedPositive(state);
	}
}