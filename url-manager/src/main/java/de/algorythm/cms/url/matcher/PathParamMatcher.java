package de.algorythm.cms.url.matcher;

import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathRuleException;

public class PathParamMatcher<K, R> extends PathMatcher<K, R> {

	public PathParamMatcher(PathRule<K, R> rule, String[] paramNames, String patternPrefix, PathMatcher<K, R> defaultMatcher) {
		super(rule, false, false, true, "**", paramNames, patternPrefix, defaultMatcher);
	}

	@Override
	protected boolean match(MatchState<K,R> state) {
		state.addParameterValue(state.getSuffix());
		matchedPositiveFinally(state);
		return true;
	}

	@Override
	public void addChild(PathMatcher<K,R> matcher) throws PathRuleException {
		throw new PathRuleException(getPatternPrefix() + " cannot have child " + matcher.getPatternPrefix() + " due to ambiguity");
	}
}
