package de.algorythm.cms.url;

public interface IPathMatchHandler<K, R> {

	/**
	 * Is called for every rule that matches while the match path continues.
	 * The current matching state is passed as argument.
	 * The state can change after execution of this method.
	 * @param state The matching state
	 */
	void matchedPrefix(IMatchState<K, R> state);
	
	/**
	 * Is called on the end of the matching if it matches a rule.
	 * @param state The matching state
	 */
	void matchedPositive(IMatchState<K, R> state);
	
	/**
	 * Is called on the end of the matching if it did not match a rule.
	 * @param state The matching state
	 */
	void matchedNegative(String path);
}
