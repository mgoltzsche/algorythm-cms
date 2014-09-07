package de.algorythm.cms.common.renderer.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.algorythm.cms.common.renderer.impl.xslt.XmlContentRenderer;

public class TestXmlContentRenderer {

	@Test
	public void testXmlContentRenderer() throws Exception {
		XmlContentRenderer testee = new XmlContentRenderer();
		String xslt = FileUtils.readFileToString(new File(getClass().getResource("/article2html.xsl").toURI()), "UTF-8");
		String xmlData = "<t:article xmlns:t=\"http://www.algorythm.de/cms/Article\" xmlns=\"http://www.algorythm.de/cms/Markup\"><t:title>Testtitel</t:title><t:content>Testinhalt <b>fett</b></t:content></t:article>";
		System.out.println(testee.render(xmlData, xslt));
	}
}
