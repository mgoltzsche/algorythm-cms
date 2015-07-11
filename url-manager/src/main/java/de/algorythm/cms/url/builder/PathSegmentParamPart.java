package de.algorythm.cms.url.builder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PathSegmentParamPart extends AUrlPart {

	private final String paramName;
	
	public PathSegmentParamPart(String paramName) {
		this.paramName = paramName;
	}
	
	@Override
	public void buildUrl(final Map<String, String> params, final StringBuilder url) {
		final String value = params.get(paramName);
		
		if (value == null)
			throw new IllegalArgumentException("Missing path parameter " + paramName);
		
		try {
			url.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Cannot encode path parameter due to unsupported encoding", e);
		}
		
		params.remove(paramName);
		buildSuffix(params, url);
	}
	
	@Override
	public String toString() {
		return '{' + paramName + '}' + getSuffix();
	}
}
