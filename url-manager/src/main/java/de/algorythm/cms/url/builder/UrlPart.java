package de.algorythm.cms.url.builder;

import java.util.Map;

public class UrlPart extends AUrlPart {

	private final String prefix;
	
	public UrlPart(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public void buildUrl(Map<String, String> params, StringBuilder url) {
		url.append(prefix);
		buildSuffix(params, url);
	}
	
	@Override
	public String toString() {
		return prefix + getSuffix();
	}
}