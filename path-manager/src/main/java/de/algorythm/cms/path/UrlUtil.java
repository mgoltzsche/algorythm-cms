package de.algorythm.cms.path;

public class UrlUtil {
	static public String normalizedPath(String path) {
		if (path.isEmpty())
			return path;
		
		int startPos = 0, endPos = path.length();
		
		if (path.charAt(0) == '/')
			startPos = 1;
		if (path.charAt(path.length() - 1) == '/')
			endPos = path.length() - 1;
		
		return path.substring(startPos, endPos);
	}
	static public int depth(String path) {
		int depth = 0;
		
		for (int i = 0; i < path.length() - 1; i++) {
			if (path.charAt(i) == '/')
				depth++;
		}
		
		return depth;
	}
}
