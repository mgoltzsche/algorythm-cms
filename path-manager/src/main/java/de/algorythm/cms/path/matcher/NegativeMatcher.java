package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.Matcher;
import de.algorythm.cms.path.PathRule;
import de.algorythm.cms.path.UrlMatchingState;

public class NegativeMatcher<K,R> extends Matcher<K,R> {
	public NegativeMatcher(R defaultResource) {
		super(new PathRule<K,R>(null, "/**", defaultResource), true, true, false, null, null, null);
	}
	@Override
	public boolean match(UrlMatchingState<K,R> state) {
		state.setNegativeResult();
		return false; // Stop matching unsuccessfully
	}
	@Override
	public String toString() {
		return "";
	}
};