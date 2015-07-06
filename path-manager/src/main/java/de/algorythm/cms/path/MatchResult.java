package de.algorythm.cms.path;

public class MatchResult<K,R> {

	private final boolean exactMatch;
	private final PathRule<K, R> rule;
	private final String path;
	
	public MatchResult(boolean exactMatch, PathRule<K, R> match, String path) {
		this.exactMatch = exactMatch;
		this.rule = match;
		this.path = path;
	}

	public boolean isExactMatch() {
		return exactMatch;
	}

	public PathRule<K, R> getMatch() {
		return rule;
	}

	public String getPath() {
		return path;
	}
}
