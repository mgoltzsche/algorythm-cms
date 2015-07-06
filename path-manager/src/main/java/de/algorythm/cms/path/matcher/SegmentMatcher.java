package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class SegmentMatcher<K,R> extends Matcher<K,R> {
	public SegmentMatcher(PathRule<K, R> rule, String segment, String patternPrefix, Matcher<K,R> defaultMatcher) {
		super(rule, false, rule != null, true, segment, patternPrefix, defaultMatcher);
	}
	@Override
	public boolean match(UrlMatchingState<K,R> state) {
//		System.out.println("Segment " + segment + "=" + state.currentSegment + (segment.equals(state.currentSegment) ? " matched" : "") + " " + (finalMatcher && state.finalSegment));
		if (segment.equals(state.getCurrentSegment()))
			return matchedPositive(state);
		else
			return matchedNegative(state);
	}
}
