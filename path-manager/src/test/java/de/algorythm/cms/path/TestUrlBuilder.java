package de.algorythm.cms.path;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.algorythm.cms.path.builder.UrlBuilderBuilder;

public class TestUrlBuilder {

	private IUrlBuilder<String> testee;
	
	@Test
	public void buildUrl_should_build_URL() throws PathRuleException {
		testee = new UrlBuilderBuilder<String>("http://example.org")
				.addRule(new PathRule<String, String>("key1", "/web/f", null))
				.addRule(new PathRule<String, String>("key2", "/web/f/{param}", null))
				.addRule(new PathRule<String, String>("key3", "/web/f/{param1}/k", null))
				.addRule(new PathRule<String, String>("key4", "/web/doc/{param+}", null))
				.build();
		
		assertBuildUrl("http://example.org/web/f", "key1");
		assertBuildUrl("http://example.org/web/f?param=x", "key1", "param", "x");
		
		assertBuildUrl("http://example.org/web/f/x", "key2", "param", "x");
		
		assertBuildUrl("http://example.org/web/f/x/k", "key3", "param1", "x");
		assertBuildUrl("http://example.org/web/f/x+x/k", "key3", "param1", "x x");
		
		assertBuildUrl("http://example.org/web/doc/my+path/x", "key4", "param", "my+path/x");
	}
	
	private void assertBuildUrl(String expectedUrl, String key, String... params) {
		String url = testee.buildUrl(key, params);
		
		assertEquals(expectedUrl, url);
	}
}
