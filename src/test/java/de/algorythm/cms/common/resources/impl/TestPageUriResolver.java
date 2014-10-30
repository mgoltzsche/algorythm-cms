package de.algorythm.cms.common.resources.impl;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;

public class TestPageUriResolver {

//	@Test
	public void testContentUriResolver() throws URISyntaxException {
		Configuration cfg = new Configuration();
		PageUriResolver testee = new PageUriResolver(cfg);
		
		URI repositoryUri = cfg.repository.toURI();
		URI pageUri1 = repositoryUri.resolve("example1.org/pages/");
		URI pageUri2 = repositoryUri.resolve("example1.org/pages/page.xml");
		URI page2Uri = repositoryUri.resolve("example1.org/pages/spec/page.xml");
		
		assertEquals("Absolute URI", pageUri2, testee.toSystemUri(pageUri2, new URI("/")));
		assertEquals("Absolute URI", pageUri2, testee.toSystemUri(pageUri2, new URI("/page.xml")));
		assertEquals("Relative URI", pageUri2, testee.toSystemUri(pageUri2, new URI("page.xml")));
		assertEquals("Relative URI", pageUri2, testee.toSystemUri(pageUri2, new URI(".")));
		assertEquals("Relative URI", page2Uri, testee.toSystemUri(pageUri2, new URI("spec/")));
		assertEquals("Relative URI", page2Uri, testee.toSystemUri(pageUri2, new URI("spec/page.xml")));
		assertEquals("Relative URI", page2Uri, testee.toSystemUri(page2Uri, new URI("page.xml")));
		assertEquals("Relative URI", page2Uri, testee.toSystemUri(page2Uri, new URI(".")));
		assertEquals("Relative URI", pageUri2, testee.toSystemUri(page2Uri, new URI("../")));
		assertEquals("Relative URI", pageUri2, testee.toSystemUri(page2Uri, new URI("../page.xml")));
		
		try {
			testee.toSystemUri(page2Uri, new URI("/../bla/page.xml"));
			throw new AssertionError("Should not allow URLs starting with '/../'");
		} catch(Exception e) {
		}
		
		try {
			testee.toSystemUri(page2Uri, new URI("/.."));
			throw new AssertionError("Should not allow URL '/..'");
		} catch(Exception e) {
		}
	}
}
