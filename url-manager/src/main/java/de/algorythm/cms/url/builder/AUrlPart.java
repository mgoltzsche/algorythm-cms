package de.algorythm.cms.url.builder;

import java.util.Map;

public abstract class AUrlPart {

	private AUrlPart suffix = UrlParamsPart.INSTANCE;

	public final AUrlPart getSuffix() {
		return suffix;
	}

	public final void setSuffix(AUrlPart suffix) {
		this.suffix = suffix;
	}
	
	public abstract void buildUrl(Map<String, String> params, StringBuilder url);
	
	protected final void buildSuffix(final Map<String, String> params, final StringBuilder url) {
		suffix.buildUrl(params, url);
	}
}