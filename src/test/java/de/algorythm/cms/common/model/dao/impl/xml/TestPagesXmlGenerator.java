package de.algorythm.cms.common.model.dao.impl.xml;

import java.io.File;
import java.util.Locale;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.loader.impl.BundleLoader;

public class TestPagesXmlGenerator {

	@Test
	public void testPagesXmlGenerator() throws Exception {
		Configuration cfg = new Configuration();
		JAXBContext jaxbCtx = JAXBContext.newInstance(Bundle.class, PageInfo.class);
		BundleLoader bundleLoader = new BundleLoader(jaxbCtx);
		PagesXmlGenerator testee = new PagesXmlGenerator(cfg, jaxbCtx);
		IBundle bundle = bundleLoader.getBundle(new File(getClass().getResource("/test-repo/example1.org/bundle.xml").toURI()));
		IPage startPage = bundleLoader.loadPages(bundle, Locale.ENGLISH);
		testee.generatePagesXml(startPage, cfg.outputDirectory);
	}
}
