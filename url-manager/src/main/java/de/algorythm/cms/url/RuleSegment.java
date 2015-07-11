package de.algorythm.cms.url;


public class RuleSegment {

	static public final int EMPTY_SEGMENT = 0;
	static public final int SEGMENT = 1;
	static public final int SEGMENT_PARAM = 2; // {param}
	static public final int PATH_PARAM = 3; // {param+}
	static private final String EMPTY = "";

	static public RuleSegment forSegment(String segment) {
		if (segment.isEmpty()) {
			return new RuleSegment(EMPTY_SEGMENT, EMPTY);
		} else if (segment.length() > 2 && segment.charAt(0) == '{' && segment.charAt(segment.length() - 1) == '}') {
			if (segment.length() > 3 && segment.charAt(segment.length() - 2) == '+') {
				final String paramName = segment.substring(1, segment.length() - 2);
				return new RuleSegment(PATH_PARAM, paramName);
			} else {
				final String paramName = segment.substring(1, segment.length() - 1);
				return new RuleSegment(SEGMENT_PARAM, paramName);
			}
		} else {
			return new RuleSegment(SEGMENT, segment);
		}
	}
	
	private final int type;
	private final String key;
	
	private RuleSegment(int type, String key) {
		this.type = type;
		this.key = key;
	}

	public int getType() {
		return type;
	}

	public String getKey() {
		return key;
	}
}
