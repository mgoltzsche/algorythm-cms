package de.algorythm.cms.common.rendering.url;

import java.util.Locale;

public class LocalizedPath {

	private final String path;
	private final Locale locale;
	
	public LocalizedPath(String path, Locale locale) {
		this.path = path;
		this.locale = locale;
	}

	public String getPath() {
		return path;
	}

	public Locale getLocale() {
		return locale;
	}
}
