package de.algorythm.cms.common.model.dao.impl.xml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.LocaleResolver;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.model.entity.impl.Site;

public class TestXmlResourceDao {

	@Test
	public void testXmlResourceDao() throws IOException {
		Configuration cfg = new Configuration();
		LocaleResolver locales = new LocaleResolver();
		XmlResourceDao testee = new XmlResourceDao(cfg, locales);
		List<ISite> sites = testee.getSites();
		
		assertEquals("site count", 3, sites.size());
		
		ISite[] expectedSites = new ISite[] {
				new Site(null, "example1.org", "Test site", Locale.ENGLISH, "/site1"),
				new Site(null, "example2.de", "Testseite", Locale.GERMAN, "/site2"),
				new Site(null, "example3.com", "example3.com", cfg.defaultLanguage, ""),
		};
		
		Arrays.sort(expectedSites);
		Collections.sort(sites);
		
		for (int i = 0; i < 3; i++) {
			ISite expectedSite = expectedSites[i];
			ISite site = sites.get(i);
			
			assertEquals("site name", expectedSite.getName(), site.getName());
			assertEquals("site title", expectedSite.getTitle(), site.getTitle());
			assertEquals("site context path", expectedSite.getContextPath(), site.getContextPath());
			assertEquals("site default locale", expectedSite.getDefaultLocale(), site.getDefaultLocale());
		}
		
		for (ISite site : sites) {
			for (IPage page : site.getPages())
				System.out.println(page);
		}
	}
}
