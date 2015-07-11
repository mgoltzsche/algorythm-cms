package de.algorythm.cms.url;

import de.algorythm.cms.url.builder.UrlBuilderBuilder;
import de.algorythm.cms.url.matcher.PathMatcherBuilder;

public class UrlManagerBuilder<K, R> {

	private final PathMatcherBuilder<K, R> pathMatcherBuilder;
	private final UrlBuilderBuilder<K> urlBuilderBuilder;
	
	public UrlManagerBuilder(String urlPrefix) {
		pathMatcherBuilder = new PathMatcherBuilder<K, R>();
		urlBuilderBuilder = new UrlBuilderBuilder<K>(urlPrefix);
	}
	
	public UrlManagerBuilder<K, R> addRule(PathRule<K, R> rule) {
		pathMatcherBuilder.addRule(rule);
		urlBuilderBuilder.addRule(rule);
		return this;
	}
	
	public IUrlManager<K, R> build() throws PathRuleException {
		IPathMatcher<K, R> pathMatcher = pathMatcherBuilder.build();
		IUrlBuilder<K> urlBuilder = urlBuilderBuilder.build();
		
		return new UrlManager<K, R>(pathMatcher, urlBuilder);
	}
}
