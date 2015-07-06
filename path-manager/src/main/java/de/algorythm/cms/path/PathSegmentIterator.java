package de.algorythm.cms.path;

public class PathSegmentIterator {

	private final String path;
	private final int maxPos;
	private int segStartPos, segEndPos, lastValidPos;
	private boolean hasNextSegment = true;
	
	public PathSegmentIterator(String path) {
		this.path = path;
		this.maxPos = path.length();
	}
	
	public boolean hasNextSegment() {
		return hasNextSegment;
	}
	
	public String nextSegment() {
		lastValidPos = segEndPos;
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
	
	public String getLastPrefix() {
		return path.substring(0, lastValidPos);
	}
	
	public String getSuffix() {
		return path.substring(segStartPos, maxPos);
	}
}
