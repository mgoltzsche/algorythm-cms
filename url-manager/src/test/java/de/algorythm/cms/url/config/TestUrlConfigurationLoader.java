package de.algorythm.cms.url.config;

import java.io.InputStream;
import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

public class TestUrlConfigurationLoader {

	@Test
	public void should_load_configuration() throws Exception {
		try (InputStream in = getClass().getResourceAsStream("/test-url-config.xml")) {
			List<UrlRule> rules = new UrlConfigurationLoader().loadConfiguration(in).getRules();
			assertRule(rules.get(0), "welcome", "/welcome", "Command A");
			assertRule(rules.get(1), "doc", "/doc/**", "Command B");
		}
	}
	
	private void assertRule(UrlRule rule, String expectedKey, String expectedPattern, String expectedCommand) {
		assertEquals("Key", expectedKey, rule.getKey());
		assertEquals("URL pattern", expectedPattern, rule.getPattern());
		assertEquals("Command", expectedCommand, rule.getCommand());
	}
}
