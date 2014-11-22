package de.algorythm.cms.common.model.dao.impl.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.loader.impl.BundleLoader;

public class TestPagesXmlGenerator {

	@Test
	public void testPagesXmlGenerator() throws Exception {
		Configuration cfg = new Configuration();
		XmlReaderFactory readerFactory = new XmlReaderFactory();
		BundleLoader bundleLoader = new BundleLoader(cfg, readerFactory);
		JAXBContext jaxbCtx = JAXBContext.newInstance(PageInfo.class);
		PagesXmlGenerator testee = new PagesXmlGenerator(cfg, jaxbCtx);
		
		IBundle bundle = bundleLoader.getBundle(new File(getClass().getResource("/test-repo/example1.org/bundle.xml").toURI()));
		testee.generatePagesXml(bundle, cfg.outputDirectory);
	}
}
