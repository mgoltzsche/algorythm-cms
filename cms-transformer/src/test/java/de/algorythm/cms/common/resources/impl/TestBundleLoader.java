package de.algorythm.cms.common.resources.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.ITheme;
import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.impl.bundle.Bundle;
import de.algorythm.cms.common.model.entity.impl.bundle.Module;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfig;
import de.algorythm.cms.common.model.entity.impl.bundle.Theme;
import de.algorythm.cms.common.resources.IInputResolver;

public class TestBundleLoader {

	@Test
	public void loadBundle_should_load_bundle() throws Exception {
		BundleLoader testee = new BundleLoader();
		URL bundleFileUrl = getClass().getResource("/");
		Path bundlePath = Paths.get(bundleFileUrl.toURI());
		IInputResolver resolver = new FileInputSourceResolver(Collections.singletonList(bundlePath));
		URI bundleUri = URI.create("/bundle-reference-models/bundle.xml");
		IBundle bundle = testee.loadBundle(bundleUri, resolver);
		
		assertNotNull("bundle", bundle);
		assertEquals("baseUri", bundleUri, bundle.getUri());
		assertEquals("defaultLocale", Locale.GERMANY, bundle.getDefaultLocale());
		assertEquals("supportedLocales count", 2, bundle.getSupportedLocales().size());
		assertEquals("dependency count", 3, bundle.getDependencies().size());
		assertEquals("output count", 2, bundle.getOutputMapping().size());
		assertTrue("output-mapping HTML", bundle.getOutputMapping().containsKey(Format.HTML));
		assertTrue("output-mapping PDF", bundle.getOutputMapping().containsKey(Format.PDF));
		
		assertEquals("1st dependency", URI.create("/org/example/cms/module1/bundle.xml"), bundle.getDependencies().iterator().next());
		assertEquals("HTML output format", Format.HTML, bundle.getOutputMapping().get(Format.HTML).getFormat());
		
		ITheme theme = bundle.getOutputMapping().get(Format.HTML).getTheme();
		
		assertEquals("1st HTML output template", URI.create("/bundle-reference-models/html/theme/transformations/MyPage.xsl"), theme.getTemplates().iterator().next());
		assertEquals("1st HTML output style", URI.create("/bundle-reference-models/css/style1.scss"), theme.getStyles().iterator().next());
		assertEquals("1st HTML output script", URI.create("/bundle-reference-models/js/script1.js"), theme.getScripts().iterator().next());
		
		assertNotNull("start page", bundle.getStartPage());
		assertEquals("start page source", URI.create("/bundle-reference-models/contents/welcome.xml"), bundle.getStartPage().getSource());
		assertEquals("1st-level children", 2, bundle.getStartPage().getPages().size());
	}

	@Test
	public void testMarshalBundle() throws Exception {
		URI uri = URI.create("/org/example/main-module/bundle.xml");
		Bundle bundle = new Bundle();
		Theme theme = new Theme();
		theme.setBaseTheme(URI.create("/org/example/module1"));
		theme.getTemplates().add(URI.create("transformations/html/mytheme/MyTheme.xsl"));
		theme.getTemplates().add(URI.create("transformations/html/mytheme/MyMenu.xsl"));
		theme.getStyles().add(URI.create("css/mytheme/style1.xsl"));
		theme.getStyles().add(URI.create("css/mytheme/style2.xsl"));
		theme.getScripts().add(URI.create("js/mytheme/script1.xsl"));
		theme.getScripts().add(URI.create("js/mytheme/script2.xsl"));
		
		bundle.setUri(uri);
		bundle.setTitle("Generated example bundle");
		bundle.getSupportedLocales().add(Locale.FRANCE);
		bundle.getDependencies().add(URI.create("/org/example/dependency"));
		bundle.getOutputMapping().put(Format.HTML, new OutputConfig(Format.HTML, theme, new Module()));
		bundle.getOutputMapping().put(Format.PDF, new OutputConfig(Format.PDF, new Theme(), new Module()));
		
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(Bundle.class, OutputConfig.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setAdapter(UriXmlAdapter.class, new UriXmlAdapter(uri));
		marshaller.marshal(bundle, writer);
		
		System.out.println(writer.toString());
	}
}
