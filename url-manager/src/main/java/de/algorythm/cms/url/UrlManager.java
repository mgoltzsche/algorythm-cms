package de.algorythm.cms.url;

import java.util.Map;

public class UrlManager<K, R> implements IUrlManager<K, R> {

	private final IPathMatcher<K, R> pathMatcher;
	private final IUrlBuilder<K> urlBuilder;

	public UrlManager(IPathMatcher<K, R> pathMatcher, IUrlBuilder<K> urlBuilder) {
		this.pathMatcher = pathMatcher;
		this.urlBuilder = urlBuilder;
	}

	@Override
	public void match(String path, IPathMatchHandler<K, R> handler) {
		pathMatcher.match(path, handler);
	}

	@Override
	public String buildUrl(K key, Map<String, String> params) {
		return urlBuilder.buildUrl(key, params);
	}

	@Override
	public String buildUrl(K key, String... params) {
		return urlBuilder.buildUrl(key, params);
	}
}
