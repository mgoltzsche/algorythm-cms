package de.algorythm.cms.path;

public class PathSegmentIterator {

	private final String path;
	private final int maxPos;
	private int lastSegStartPos, segStartPos, segEndPos;
	private boolean hasNextSegment = true;
	
	public PathSegmentIterator(String path) {
		this.path = path;
		this.maxPos = path.length();
	}
	
	public boolean hasNextSegment() {
		return hasNextSegment;
	}
	
	public String nextSegment() {
		lastSegStartPos = segStartPos;
		segEndPos = path.indexOf('/', segStartPos);
		hasNextSegment = segEndPos > -1;
		
		if (hasNextSegment) {
			final String segment = path.substring(segStartPos, segEndPos);
			segStartPos = segEndPos + 1;
			
			return segment;
		} else {
			segEndPos = maxPos;
			
			return path.substring(segStartPos, maxPos);
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public String getCurrentPrefix() {
		return path.substring(0, segEndPos);
	}
	
	public String getCurrentSuffix() {
		return path.substring(lastSegStartPos, maxPos);
	}
}
