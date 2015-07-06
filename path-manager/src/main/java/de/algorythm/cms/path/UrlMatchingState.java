package de.algorythm.cms.path;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class UrlMatchingState<K,R> {
	
	static private String EMPTY = "";
	
	private final String path;
	private boolean finalSegment;
	private String currentSegment;
	private int endPos, lastEndPos;
	private final int maxStrPos;
	private List<String> parameterValues = new LinkedList<>();
	private PathRule<K, R> lastMatchingRule;
	private MatchResult<K,R> result;
	
	public UrlMatchingState(final String path) {
		this.result = null;
		this.path = path;
		
		if (path.isEmpty()) {
			currentSegment = EMPTY;
			endPos = -1;
			this.maxStrPos = 0;
		} else {
			if (path.charAt(0) != '/')
				throw new IllegalArgumentException("Cannot match relative path");
			
			if (path.length() == 1) {
				currentSegment = EMPTY;
				endPos = -1;
				this.maxStrPos = 0;
			} else {
				maxStrPos = path.charAt(path.length() - 1) == '/'
						? path.length() - 1 : path.length();
			}
			
			finalSegment = false;
			nextSegment();
		}
	}
	public String getPath() {
		return path;
	}
	private String getLastPrefix() {
		return path.substring(0, lastEndPos);
	}
	public String getSuffix() {
		return path.substring(endPos, path.length());
	}
	public boolean isFinalSegment() {
		return finalSegment;
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
	public MatchResult<K,R> getResult() {
		return result;
	}
	public void setResult(MatchResult<K, R> result) {
		this.result = result;
	}
	public void setNegativeResult() {
		result = new MatchResult<>(false, lastMatchingRule, getLastPrefix());
	}
	public boolean nextSegment() {
		if (finalSegment)
			return false;
		
		int nextStartPos = endPos + 1;
		int currentEndPos = path.indexOf('/', nextStartPos);
		
		if (currentEndPos == -1)
			currentEndPos = maxStrPos;
		System.out.println(nextStartPos + "  "+ currentEndPos);
		currentSegment = path.substring(nextStartPos, currentEndPos);
		lastEndPos = endPos;
		endPos = currentEndPos;
		System.out.println(currentSegment);
		finalSegment = endPos == maxStrPos;
		return true;
	}
	
	/*public String createPath() {
		return createPath(startSegmentPos, segments.length);
	}
	
	public String createPath(final int startSegmentPos, final int endSegmentPos) {
		final StringBuilder url = new StringBuilder(255);
		
		for (int i = startSegmentPos; i < endSegmentPos; i++) {
			url.append('/').append(segments[i]);
		}
		
		if (!parameters.isEmpty()) {
			url.append('?');
			
			final Iterator<Entry<String, String>> paramIter = parameters.entrySet().iterator();
			Entry<String, String> param = paramIter.next();
			
			url.append(encode(param.getKey())).append('=').append(encode(param.getValue()));
			
			while (paramIter.hasNext()) {
				url.append('&').append(encode(param.getKey())).append('=').append(encode(param.getValue()));
			}
		}
		
		return url.toString();
	}*/
	
	private String encode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
