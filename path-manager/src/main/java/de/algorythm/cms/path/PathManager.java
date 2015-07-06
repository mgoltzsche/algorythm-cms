package de.algorythm.cms.path;


public class PathManager<K,R> implements IPathManager<K,R> {
	
	private final Matcher<K,R> matcher;
	
	public PathManager(Matcher<K,R> matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public MatchResult<K, R> match(String path) {
		UrlMatchingState<K,R> state = new UrlMatchingState<>(path);
		matcher.match(state);
		return state.getResult();
	}
	
	/*public String createUrl(K key, String... paramValues) {
		
	}*/
	
	@Override
	public String toString() {
		return "Matcher [" + matcher + ']';
	}
}
