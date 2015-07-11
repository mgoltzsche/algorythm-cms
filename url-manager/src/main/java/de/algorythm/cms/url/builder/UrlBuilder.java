package de.algorythm.cms.url.builder;

import java.util.HashMap;
import java.util.Map;

import de.algorythm.cms.url.IUrlBuilder;

public class UrlBuilder<K> implements IUrlBuilder<K> {

	private final Map<K, AUrlPart> urlMap;

	public UrlBuilder(Map<K, AUrlPart> urlMap) {
		this.urlMap = urlMap;
	}

	@Override
	public String buildUrl(K key, String... params) {
		if (params.length % 2 != 0)
			throw new IllegalStateException("URL parameter array size must be even");
		
		final Map<String, String> paramMap = new HashMap<>();
		
		for (int i = 0; i < params.length; i += 2)
			paramMap.put(params[i], params[i + 1]);
		
		return buildUrl(key, paramMap);
	}

	@Override
	public String buildUrl(K key, Map<String, String> params) {
		final StringBuilder url = new StringBuilder();
		final AUrlPart urlBuilder = urlMap.get(key);
		
		if (urlBuilder == null)
			throw new IllegalArgumentException("Unknown URL key " + key);
		
		urlBuilder.buildUrl(params, url);
		
		return url.toString();
	}
}
