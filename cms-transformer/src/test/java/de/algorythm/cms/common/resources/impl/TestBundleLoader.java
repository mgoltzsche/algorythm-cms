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

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.ITheme;
import de.algorythm.cms.common.model.entity.bundle.OutputFormat;
import de.algorythm.cms.common.model.entity.impl.bundle.Bundle;
import de.algorythm.cms.common.model.entity.impl.bundle.Module;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfig;
import de.algorythm.cms.common.model.entity.impl.bundle.Theme;
import de.algorythm.cms.common.resources.ISourcePathResolver;

public class TestBundleLoader {

	@Test
	public void loadBundle_should_load_bundle() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Bundle.class);
		BundleLoader2 testee = new BundleLoader2(jaxbContext);
		URL bundleFileUrl = getClass().getResource("/");
		Path bundlePath = Paths.get(bundleFileUrl.toURI());
		ISourcePathResolver resolver = new ResourceResolver(Collections.singletonList(bundlePath));
		URI bundleUri = URI.create("/bundle-reference-model/bundle.xml");
		IBundle bundle = testee.getBundle(bundleUri, resolver);
		
		assertNotNull("bundle", bundle);
		assertEquals("baseUri", bundleUri, bundle.getUri());
		assertEquals("defaultLocale", Locale.GERMANY, bundle.getDefaultLocale());
		assertEquals("supportedLocales count", 2, bundle.getSupportedLocales().size());
		assertEquals("dependency count", 3, bundle.getDependencies().size());
		assertEquals("output count", 2, bundle.getOutputMapping().size());
		assertTrue("output-mapping HTML", bundle.getOutputMapping().containsKey(OutputFormat.HTML));
		assertTrue("output-mapping PDF", bundle.getOutputMapping().containsKey(OutputFormat.PDF));
		
		assertEquals("1st dependency", URI.create("/org/example/cms/module1/bundle.xml"), bundle.getDependencies().iterator().next());
		assertEquals("HTML output format", OutputFormat.HTML, bundle.getOutputMapping().get(OutputFormat.HTML).getFormat());
		
		ITheme theme = bundle.getOutputMapping().get(OutputFormat.HTML).getTheme();
		
		assertEquals("1st HTML output template", URI.create("transformations/html/MyPage.xsl"), theme.getTemplates().iterator().next());
		assertEquals("1st HTML output style", URI.create("css/style1.scss"), theme.getStyles().iterator().next());
		assertEquals("1st HTML output script", URI.create("js/script1.js"), theme.getScripts().iterator().next());
	}

	@Test
	public void testMarshalBundle() throws Exception {
		Bundle bundle = new Bundle();
		Theme theme = new Theme();
		theme.setBaseTheme(URI.create("/org/example/module1"));
		theme.getTemplates().add(URI.create("transformations/html/mytheme/MyTheme.xsl"));
		theme.getTemplates().add(URI.create("transformations/html/mytheme/MyMenu.xsl"));
		theme.getStyles().add(URI.create("css/mytheme/style1.xsl"));
		theme.getStyles().add(URI.create("css/mytheme/style2.xsl"));
		theme.getScripts().add(URI.create("js/mytheme/script1.xsl"));
		theme.getScripts().add(URI.create("js/mytheme/script2.xsl"));
		
		bundle.getSupportedLocales().add(Locale.FRENCH);
		bundle.getDependencies().add(URI.create("/org/example/dependency"));
		bundle.getOutputMapping().put(OutputFormat.HTML, new OutputConfig(OutputFormat.HTML, theme, new Module()));
		bundle.getOutputMapping().put(OutputFormat.PDF, new OutputConfig(OutputFormat.PDF, new Theme(), new Module()));
		
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(Bundle.class, OutputConfig.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(bundle, writer);
		
		System.out.println(writer.toString());
	}
}
