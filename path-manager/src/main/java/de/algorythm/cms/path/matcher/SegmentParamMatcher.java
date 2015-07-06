package de.algorythm.cms.path.matcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class SegmentParamMatcher<K,R> extends Matcher<K,R> {
	public SegmentParamMatcher(PathRule<K, R> rule, String patternPrefix, Matcher<K,R> defaultMatcher) {
		super(rule, false, rule != null, false, "*", patternPrefix, defaultMatcher);
	}
	@Override
	public boolean match(UrlMatchingState<K, R> state) {
		final String pathSegment = state.getCurrentSegment();
		
		try {
			state.addParameterValue(URLDecoder.decode(pathSegment, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Cannot decode URL path segment " + pathSegment);
		}
		
		return matchedPositive(state);
	}
}