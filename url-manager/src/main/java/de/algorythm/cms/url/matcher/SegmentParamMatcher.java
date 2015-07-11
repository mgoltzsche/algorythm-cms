package de.algorythm.cms.url.matcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import de.algorythm.cms.url.PathRule;

public class SegmentParamMatcher<K, R> extends PathMatcher<K, R> {

	public SegmentParamMatcher(PathRule<K, R> rule, String[] paramNames, String patternPrefix, PathMatcher<K, R> defaultMatcher) {
		super(rule, false, false, rule != null, "*", paramNames, patternPrefix, defaultMatcher);
	}

	@Override
	protected boolean match(MatchState<K, R> state) {
		final String pathSegment = state.getCurrentSegment();
		
		try {
			state.addParameterValue(URLDecoder.decode(pathSegment, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Cannot decode URL path segment " + pathSegment);
		}
		
		return matchedPositive(state);
	}
}