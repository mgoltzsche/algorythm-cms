package de.algorythm.cms.path.matcher;

import de.algorythm.cms.path.PathRuleException;
import de.algorythm.cms.path.IPathMatchHandler;
import de.algorythm.cms.path.IPathMatcher;
import de.algorythm.cms.path.PathRule;

public abstract class PathMatcher<K, R> implements IPathMatcher<K, R> {

	private PathMatcher<K, R> child, sibling;
	protected final String segment;
	private final String patternPrefix;
	private final boolean terminator, finalMatcher;
	private final PathRule<K, R> rule;
	private final String[] paramNames;
	/**
	 * Used to detect ambiguities.
	 */
	protected final boolean successorAllowed;
	
	public PathMatcher(PathRule<K, R> rule, boolean terminator, boolean successorAllowed, boolean finalMatcher, String segment, String[] paramNames, String patternPrefix, PathMatcher<K,R> defaultMatcher) {
		this.terminator = terminator;
		this.successorAllowed = successorAllowed;
		this.finalMatcher = finalMatcher;
		this.segment = segment;
		this.paramNames = paramNames;
		this.patternPrefix = patternPrefix;
		this.rule = rule;
		child = sibling = defaultMatcher;
	}
	
	public final PathMatcher<K, R> getChild() {
		return child;
	}
	
	public final String getPatternPrefix() {
		return patternPrefix;
	}
	
	public boolean isFinalMatcher() {
		return finalMatcher;
	}
	
	@Override
	public final void match(final String path, final IPathMatchHandler<K, R> handler) {
		match(new MatchState<>(path, handler));
	}
	
	protected abstract boolean match(MatchState<K, R> state);
	
	/**
	 * To be used in match method to match children.
	 * @param state The path matching state
	 * @return
	 */
	protected final boolean matchedPositive(final MatchState<K,R> state) {
		if (finalMatcher) {
			if (state.isFinalSegment()) { // Stop matching successfully
				matchedPositiveFinally(state);
				return true;
			}
			
			state.matchedPrefix(rule, paramNames);
		}
		
		if (state.nextSegment()) {
			return child.match(state); // Match child
		} else {
			state.matchedNegative();
			return false;
		}
	}
	
	protected final void matchedPositiveFinally(final MatchState<K, R> state) {
		state.matchedPositive(rule, paramNames);
	}
	
	protected final boolean matchedNegative(final MatchState<K,R> state) {
		return sibling.match(state);
	}
	
	public void addChild(final PathMatcher<K,R> matcher) throws PathRuleException {
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
	
	private final void addSibling(final PathMatcher<K,R> matcher) throws PathRuleException {
		PathMatcher<K,R> lastSibling = this;
		
		while (!lastSibling.sibling.terminator && lastSibling.sibling.successorAllowed) {
			lastSibling = lastSibling.sibling;
		}
		
		if (!matcher.successorAllowed && !lastSibling.successorAllowed)
			ambiguousPatterError(lastSibling.patternPrefix, matcher.patternPrefix);
		
		matcher.sibling = lastSibling.sibling;
		lastSibling.sibling = matcher;
	}
	
	private final void ambiguousPatterError(String pattern1, String pattern2) throws PathRuleException {
		throw new PathRuleException("Path pattern " + pattern1 + " and " + pattern2 + " are ambiguous");
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		if (sibling.terminator) {			
			appendSlash(str);
			appendWithChildren(str);
		} else {
			appendSlash(str);
			str.append('{');
			appendWithChildren(str);
			PathMatcher<K,R> currentSibling = sibling;
			
			do {
				str.append('|');
				currentSibling.appendWithChildren(str);
				currentSibling = currentSibling.sibling;
			} while (!currentSibling.terminator);
			
			str.append('}');
		}
		
		return str.toString();
	}
	
	private final void appendWithChildren(StringBuilder str) {
		str.append(segment);
		
		if (finalMatcher)
			str.append('!');
		
		if (!child.terminator)
			str.append(child.toString());
	}
	
	protected void appendSlash(StringBuilder str) {
		str.append('/');
	}
}
