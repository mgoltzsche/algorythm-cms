package de.algorythm.cms.common.model.dao.impl.xml;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.loader.impl.BundleLoader;

public class TestXmlResourceDao {

	@Test
	public void testXmlResourceDao() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Bundle.class, PageInfo.class);
		BundleLoader testee = new BundleLoader(jaxbContext);
		Path bundleXml = Paths.get(getClass().getResource("/test-repo/example1.org/bundle.xml").toURI());
		IBundle bundle = testee.getBundle(bundleXml);
		IBundle expectedBundle = createSite("example.org", "My example site", Locale.ENGLISH, "/site1");
		
		try {
			assertEquals("site name", expectedBundle.getName(), bundle.getName());
			assertEquals("site title", expectedBundle.getTitle(), bundle.getTitle());
			assertEquals("site context path", expectedBundle.getContextPath(), bundle.getContextPath());
			assertEquals("site default locale", expectedBundle.getDefaultLocale(), bundle.getDefaultLocale());
		} catch(AssertionError e) {
			throw new AssertionError(expectedBundle.getName() + " - " + e.getMessage());
		}
		
		printPage(testee.loadPages(bundle, Locale.ENGLISH), 0);
	}
	
	private void printPage(IPage p, int depth) {
		String s = "";
		
		for (int i = 0; i < depth; i++)
			s += "  ";
		
		System.out.println(s + p.getPath() + "/ (" + p.getTitle() + ", " + p.getNavigationTitle() + ")");
		
		for (IPage child : p.getPages())
			printPage(child, depth + 1);
	}
	
	private IBundle createSite(String name, String title, Locale locale, String contextPath) {
		final Bundle bundle = new Bundle();
		
		bundle.setName(name);
		bundle.setTitle(title);
		bundle.setDefaultLocale(locale);
		bundle.setContextPath(contextPath);
		
		return bundle;
	}
}
