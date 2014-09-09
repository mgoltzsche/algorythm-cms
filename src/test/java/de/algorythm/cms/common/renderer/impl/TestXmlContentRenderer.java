package de.algorythm.cms.common.renderer.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import de.algorythm.cms.common.renderer.impl.xslt.XmlContentRenderer;

public class TestXmlContentRenderer {

	@Test
	public void testPageRenderer() throws Exception {
		XmlContentRenderer testee = new XmlContentRenderer();
		
		System.out.println(testee.render(jarFile("/test-content/page.xml")));
	}
	
	@Test
	public void testXmlContentRenderer() throws Exception {
		XmlContentRenderer testee = new XmlContentRenderer();
		
		System.out.println(testee.render(jarFile("/test-content/article.xml")));
	}
	
	private File jarFile(final String filePath) throws URISyntaxException {
		final URL fileUrl = getClass().getResource(filePath);
		
		if (fileUrl == null)
			throw new IllegalStateException("Missing test file: " + filePath);
		
		return new File(fileUrl.toURI());
	}
}
