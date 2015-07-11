package de.algorythm.cms.path;

public class PathRule<K, R> implements Comparable<PathRule<K, R>> {

	static public int depth(String path) {
		final PathSegmentIterator iter = new PathSegmentIterator(path);
		int depth = 0;
		
		while (iter.hasNextSegment()) {
			iter.nextSegment();
			depth++;
		}
		
		return depth;
	}
	
	private final K key;
	private final String pattern;
	private final R resource;
	private final int depth;
	
	public PathRule(K key, String pattern, R resource) {
		if (!pattern.isEmpty() && pattern.charAt(0) != '/')
			throw new IllegalArgumentException(pattern + " is relative");
		
		this.key = key;
		this.resource = resource;
		this.depth = pattern == null ? 0 : depth(pattern);
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
