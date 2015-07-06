package de.algorythm.cms.path;

public abstract class Matcher<K,R> {
	
	protected Matcher<K,R> child, sibling;
	protected final String segment, patternPrefix;
	protected final boolean terminator, finalMatcher;
	private final PathRule<K, R> rule;
	/**
	 * Used to detect ambiguities.
	 */
	protected final boolean siblingAllowed;
	
	public Matcher(PathRule<K, R> rule, boolean terminator, boolean finalMatcher, boolean siblingAllowed, String segment, String patternPrefix, Matcher<K,R> defaultMatcher) {
		this.terminator = terminator;
		this.finalMatcher = finalMatcher;
		this.siblingAllowed = siblingAllowed;
		this.segment = segment;
		this.patternPrefix = patternPrefix;
		this.rule = rule;
		child = sibling = defaultMatcher;
	}
	public abstract boolean match(UrlMatchingState<K,R> state);
	protected final boolean matchedPositive(UrlMatchingState<K,R> state) {
		if (state.isFinalSegment() && finalMatcher) { // Stop matching successfully
			state.setResult(new MatchResult<K,R>(true, rule, state.getPath()));
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
	protected final boolean matchedNegative(UrlMatchingState<K,R> state) {
		return sibling.match(state);
	}
	public void addChild(Matcher<K,R> matcher) {
		if (child.terminator) {
			child = matcher;
		} else {
			if (!child.siblingAllowed || !matcher.siblingAllowed)
				throw new IllegalStateException("Path pattern " + child.patternPrefix + " and " + matcher.patternPrefix + " are ambiguous");
			
			child.addSibling(matcher);
		}
	}
	private final void addSibling(Matcher<K,R> matcher) {
		Matcher<K,R> lastSibling = this;
		
		while (!lastSibling.sibling.terminator) {
			lastSibling = lastSibling.sibling;
		}
		
		lastSibling.sibling = matcher;
	}
	public final String getPatternPrefix() {
		return patternPrefix;
	}
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append(sibling.terminator ? "/" : "/{");
		appendWithChildren(str);
		
		if (!sibling.terminator) {
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
		
		if (finalMatcher)
			str.append('!');
		
		if (!child.terminator)
			str.append(child.toString());
	}
}
