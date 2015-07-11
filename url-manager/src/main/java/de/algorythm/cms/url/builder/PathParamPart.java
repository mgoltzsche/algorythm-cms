package de.algorythm.cms.url.builder;

import java.util.Map;

public class PathParamPart extends AUrlPart {

	private final String paramName;
	
	public PathParamPart(String paramName) {
		this.paramName = paramName;
	}
	
	@Override
	public void buildUrl(final Map<String, String> params, final StringBuilder url) {
		final String value = params.get(paramName);
		
		if (value == null)
			throw new IllegalArgumentException("Missing path parameter " + paramName);
		
		params.remove(paramName);
		url.append(value);
		buildSuffix(params, url);
	}
	
	@Override
	public String toString() {
		return '{' + paramName + "+}" + getSuffix();
	}
}
