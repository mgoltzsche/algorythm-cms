package de.algorythm.cms.common.model.dao.impl.xml;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.LocaleResolver;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.model.entity.impl.SiteInfo;
import de.algorythm.cms.common.model.index.impl.XmlSiteIndex;

public class TestXmlResourceDao {

	@Test
	public void testXmlResourceDao() throws Exception {
		Configuration cfg = new Configuration();
		LocaleResolver locales = new LocaleResolver();
		XmlReaderFactory readerFactory = new XmlReaderFactory();
		XmlSiteIndex testee = new XmlSiteIndex(cfg, locales, readerFactory);
		List<ISite> sites = testee.getSites();
		
		assertEquals("site count", 3, sites.size());
		
		ISite[] expectedSites = new ISite[] {
				createSite("example1.org", "My example site 1", Locale.ENGLISH, "/site1"),
				createSite("example2.de", "Testseite", Locale.GERMAN, "/site2"),
				createSite("example3.com", "example3.com", cfg.defaultLanguage, "/"),
		};
		
		Arrays.sort(expectedSites);
		Collections.sort(sites);
		
		for (int i = 0; i < 3; i++) {
			ISite expectedSite = expectedSites[i];
			ISite site = sites.get(i);
			
			try {
				assertEquals("site name", expectedSite.getName(), site.getName());
				assertEquals("site title", expectedSite.getTitle(), site.getTitle());
				assertEquals("site context path", expectedSite.getContextPath(), site.getContextPath());
				assertEquals("site default locale", expectedSite.getDefaultLocale(), site.getDefaultLocale());
			} catch(AssertionError e) {
				throw new AssertionError(expectedSite.getName() + " - " + e.getMessage());
			}
		}
		
		for (ISite site : sites) {
			System.out.println(site);
			
			final IPage startPage = site.getStartPage();
			
			if (startPage != null) {
				System.out.println("  " + startPage + " (" + startPage.getTitle() + ", " + startPage.getNavigationTitle() + ")");
				
				for (IPage page : startPage.getPages())
					System.out.println("    " + page);
			}
		}
	}
	
	private ISite createSite(String name, String title, Locale locale, String contextPath) {
		final SiteInfo siteInfo = new SiteInfo();
		
		siteInfo.setName(name);
		siteInfo.setTitle(title);
		siteInfo.setDefaultLocale(locale);
		siteInfo.setContextPath(contextPath);
		
		return siteInfo;
	}
}
