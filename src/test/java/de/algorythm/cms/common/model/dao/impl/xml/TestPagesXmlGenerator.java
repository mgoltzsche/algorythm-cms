package de.algorythm.cms.common.model.dao.impl.xml;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.LocaleResolver;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.model.entity.impl.PageInfo;

public class TestPagesXmlGenerator {

	@Test
	public void testPagesXmlGenerator() throws Exception {
		Configuration cfg = new Configuration();
		LocaleResolver locales = new LocaleResolver();
		XmlReaderFactory readerFactory = new XmlReaderFactory();
		XmlResourceDao pageInfoReader = new XmlResourceDao(cfg, locales, readerFactory);
		JAXBContext jaxbCtx = JAXBContext.newInstance(PageInfo.class);
		PagesXmlGenerator testee = new PagesXmlGenerator(cfg, jaxbCtx);
		
		for (ISite site : pageInfoReader.getSites())
			testee.generatePagesXml(site);
	}
}
