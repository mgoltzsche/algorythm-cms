package de.algorythm.cms.common.renderer.impl;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.renderer.impl.xml.XmlContentRenderer;

public class TestXmlContentRenderer {

	private XmlContentRenderer testee;

	@Before
	public void before() throws Exception {
		testee = new XmlContentRenderer(new Configuration(), new XmlReaderFactory());
	}
	
/*	@Test
	public void testPageRenderer() throws Exception {
		final StringWriter writer = new StringWriter();
		
		testee.render(jarFile("/test-content/page.xml"), writer);
		System.out.println(writer.toString());
	}
	
	@Test
	public void testContentRenderer() throws Exception {
		final StringWriter writer = new StringWriter();
		
		testee.render(jarFile("/test-content/article.xml"), writer);
		System.out.println(writer.toString());
	}*/
	
	private File jarFile(final String filePath) throws URISyntaxException {
		final URL fileUrl = getClass().getResource(filePath);
		
		if (fileUrl == null)
			throw new IllegalStateException("Missing test file: " + filePath);
		
		return new File(fileUrl.toURI());
	}
}
