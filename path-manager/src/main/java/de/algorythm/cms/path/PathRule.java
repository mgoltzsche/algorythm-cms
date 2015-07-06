package de.algorythm.cms.path;

public class PathRule<K, R> implements Comparable<PathRule<K, R>> {

	private final K key;
	private final String pattern;
	private final R resource;
	private final int depth;
	
	public PathRule(K key, String pattern, R resource) {
		this.key = key;
		this.resource = resource;
		this.depth = UrlUtil.depth(UrlUtil.normalizedPath(pattern));
		this.pattern = pattern;
	}
	
	public K getKey() {
		return key;
	}

	public String getPattern() {
		return pattern;
	}

	public R getResource() {
		return resource;
	}
	
	public int getDepth() {
		return depth;
	}

	@Override
	public int compareTo(PathRule<K, R> o) {
		return depth - o.depth;
	}

	@Override
	public String toString() {
		return "PathRule [" + key + ":" + pattern + "->" + resource + ']';
	}
}
