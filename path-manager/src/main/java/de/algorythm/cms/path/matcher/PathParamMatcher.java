package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class PathParamMatcher<K,R> extends Matcher<K,R> {
	public PathParamMatcher(PathRule<K, R> rule, String patternPrefix, Matcher<K,R> defaultMatcher) {
		super(rule, false, true, false, "**", patternPrefix, defaultMatcher);
	}
	@Override
	public boolean match(UrlMatchingState<K,R> state) {
		state.addParameterValue(state.getSuffix());
		return true;
	}
	@Override
	public void addChild(Matcher<K,R> matcher) {
		throw new IllegalStateException(patternPrefix + " cannot have child " + matcher.getPatternPrefix() + " due to ambiguity");
	}
}
