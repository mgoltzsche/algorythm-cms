package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import static org.junit.Assert.*;

public class TestContentUriResolver {

	@Test
	public void testContentUriResolver() throws URISyntaxException {
		Configuration cfg = new Configuration();
		ContentUriResolver testee = new ContentUriResolver(cfg);
		
		URI repositoryUri = cfg.repository.toURI();
		URI pageUri = repositoryUri.resolve("example1.org/pages/page.xml");
		URI articleUri = repositoryUri.resolve("example1.org/contents/article.xml");
		URI article1Uri = repositoryUri.resolve("example1.org/contents/spec/article.xml");
		
		assertEquals("Absolute URI", articleUri, testee.toSystemUri(pageUri, new URI("/example1.org/article.xml")));
		assertEquals("Relative URI", article1Uri, testee.toSystemUri(articleUri, new URI("spec/article.xml")));
		assertEquals("Relative URI from page", article1Uri, testee.toSystemUri(pageUri, new URI("spec/article.xml")));
	}
}
