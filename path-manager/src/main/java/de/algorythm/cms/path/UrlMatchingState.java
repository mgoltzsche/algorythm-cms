package de.algorythm.cms.path;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class UrlMatchingState<K, R> {

	private final PathSegmentIterator segmentIter;
	private String currentSegment;
	private List<String> parameterValues = new LinkedList<>();
	private PathRule<K, R> lastMatchingRule;
	private MatchResult<K, R> result;

	public UrlMatchingState(final String path) {
		this.segmentIter = new PathSegmentIterator(path);
		this.result = null;

		nextSegment();
	}

	public String getPath() {
		return segmentIter.getPath();
	}

	private String getLastPrefix() {
		return segmentIter.getLastPrefix();
	}

	public String getSuffix() {
		return segmentIter.getSuffix();
	}

	public boolean isFinalSegment() {
		return !segmentIter.hasNextSegment();
	}

	public String getCurrentSegment() {
		return currentSegment;
	}

	public void setLastMatchingRule(PathRule<K, R> rule) {
		this.lastMatchingRule = rule;
	}

	public void addParameterValue(String value) {
		parameterValues.add(value);
	}

	public MatchResult<K, R> getResult() {
		return result;
	}

	public void setResult(MatchResult<K, R> result) {
		this.result = result;
	}

	public void setNegativeResult() {
		result = new MatchResult<>(false, lastMatchingRule, getLastPrefix());
	}

	public boolean nextSegment() {
		if (segmentIter.hasNextSegment()) {
			currentSegment = segmentIter.nextSegment();
			return true;
		}

		return false;
	}

	/*
	 * public String createPath() { return createPath(startSegmentPos,
	 * segments.length); }
	 * 
	 * public String createPath(final int startSegmentPos, final int
	 * endSegmentPos) { final StringBuilder url = new StringBuilder(255);
	 * 
	 * for (int i = startSegmentPos; i < endSegmentPos; i++) {
	 * url.append('/').append(segments[i]); }
	 * 
	 * if (!parameters.isEmpty()) { url.append('?');
	 * 
	 * final Iterator<Entry<String, String>> paramIter =
	 * parameters.entrySet().iterator(); Entry<String, String> param =
	 * paramIter.next();
	 * 
	 * url.append(encode(param.getKey())).append('=').append(encode(param.getValue
	 * ()));
	 * 
	 * while (paramIter.hasNext()) {
	 * url.append('&').append(encode(param.getKey()
	 * )).append('=').append(encode(param.getValue())); } }
	 * 
	 * return url.toString(); }
	 */

	private String encode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
