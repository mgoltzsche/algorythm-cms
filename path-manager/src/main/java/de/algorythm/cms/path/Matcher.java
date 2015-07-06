package de.algorythm.cms.path;


public abstract class Matcher<K,R> {
	
	private Matcher<K,R> child, sibling;
	protected final String segment, patternPrefix;
	protected final boolean terminator, finalMatcher;
	private final PathRule<K, R> rule;
	/**
	 * Used to detect ambiguities.
	 */
	protected final boolean successorAllowed;
	
	public Matcher(PathRule<K, R> rule, boolean terminator, boolean finalMatcher, boolean successorAllowed, String segment, String patternPrefix, Matcher<K,R> defaultMatcher) {
		this.terminator = terminator;
		this.finalMatcher = finalMatcher;
		this.successorAllowed = successorAllowed;
		this.segment = segment;
		this.patternPrefix = patternPrefix;
		this.rule = rule;
		child = sibling = defaultMatcher;
	}

	public abstract boolean match(UrlMatchingState<K,R> state);
	
	public Matcher<K, R> getChild() {
		return child;
	}
	
	protected final boolean matchedPositive(UrlMatchingState<K,R> state) {
		System.out.println(state.getCurrentSegment() + child);
		if (state.isFinalSegment() && finalMatcher) { // Stop matching successfully
			matchedPositiveFinally(state);
			return true;
		} else {
			state.setLastMatchingRule(rule);
			
			if (state.nextSegment()) {
				return child.match(state); // Match child
			} else {
				state.setNegativeResult();
				return false;
			}
		}
	}
	
	protected final void matchedPositiveFinally(UrlMatchingState<K,R> state) {
		state.setResult(new MatchResult<K,R>(true, rule, state.getPath()));
	}
	
	protected final boolean matchedNegative(UrlMatchingState<K,R> state) {
		return sibling.match(state);
	}
	
	public void addChild(Matcher<K,R> matcher) {
		if (child.terminator) {
			child = matcher;
		} else {
			if (child.successorAllowed) {
				child.addSibling(matcher);
			} else {
				if (!matcher.successorAllowed)
					ambiguousPatterError(child.patternPrefix, matcher.patternPrefix);
				
				matcher.sibling = child;
				child = matcher;
			}
		}
	}
	
	private final void addSibling(Matcher<K,R> matcher) {
		Matcher<K,R> lastSibling = this;
		
		while (!lastSibling.sibling.terminator && lastSibling.sibling.successorAllowed) {
			lastSibling = lastSibling.sibling;
		}
		
		if (!matcher.successorAllowed && !lastSibling.successorAllowed)
			ambiguousPatterError(lastSibling.patternPrefix, matcher.patternPrefix);
		
		matcher.sibling = lastSibling.sibling;
		lastSibling.sibling = matcher;
	}
	
	private void ambiguousPatterError(String pattern1, String pattern2) {
		throw new IllegalStateException("Path pattern " + pattern1 + " and " + pattern2 + " are ambiguous");
	}
	
	public final String getPatternPrefix() {
		return patternPrefix;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		if (sibling.terminator) {
			appendSlash(str);
			appendWithChildren(str);
		} else {
			appendSlash(str);
			str.append("{");
			appendWithChildren(str);
			Matcher<K,R> currentSibling = sibling;
			do {
				str.append('|');
				currentSibling.appendWithChildren(str);
				currentSibling = currentSibling.sibling;
			} while (!currentSibling.terminator);
			str.append('}');
		}
		
		return str.toString();
	}
	
	private void appendWithChildren(StringBuilder str) {
		str.append(segment);
		
		if (!child.terminator)
			str.append(child.toString());
	}
	
	protected void appendSlash(StringBuilder str) {
		str.append('/');
	}
}
