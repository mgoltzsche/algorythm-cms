package de.algorythm.cms.url;

import de.algorythm.cms.url.builder.UrlBuilderBuilder;
import de.algorythm.cms.url.matcher.PathMatcherBuilder;

public class UrlManagerBuilder<K, R> {

	private final PathMatcherBuilder<K, R> matcherBuilder;
	private final UrlBuilderBuilder<K> builderBuilder;
	
	public UrlManagerBuilder(String urlPrefix) {
		matcherBuilder = new PathMatcherBuilder<K, R>();
		builderBuilder = new UrlBuilderBuilder<K>(urlPrefix);
	}
	
	public UrlManagerBuilder<K, R> addRule(PathRule<K, R> rule) {
		matcherBuilder.addRule(rule);
		builderBuilder.addRule(rule);
		
		return this;
	}
	
	public IUrlManager<K, R> build() throws PathRuleException {
		IPathMatcher<K, R> pathMatcher = matcherBuilder.build();
		IUrlBuilder<K> urlBuilder =      builderBuilder.build();
		
		return new UrlManager<K, R>(pathMatcher, urlBuilder);
	}
}
