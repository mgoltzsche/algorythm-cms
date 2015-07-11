package de.algorythm.cms.path.matcher;

public class NegativeMatcher<K, R> extends PathMatcher<K, R> {

	public NegativeMatcher() {
		super(null, true, true, true, null, null, null, null);
	}

	@Override
	protected boolean match(MatchState<K, R> state) {
		state.matchedNegative();
		return false; // Stop matching unsuccessfully
	}

	@Override
	public String toString() {
		return "";
	}
};