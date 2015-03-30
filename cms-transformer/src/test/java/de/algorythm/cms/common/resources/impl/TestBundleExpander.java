package de.algorythm.cms.common.resources.impl;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getLast;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

import org.junit.Test;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.resources.ISourcePathResolver;

public class TestBundleExpander {

	@Test
	public void expandBundle_should_expand_bundle() throws Exception {
		BundleLoader loader = new BundleLoader();
		BundleExpander testee = new BundleExpander(loader);
		URL bundleFileUrl = getClass().getResource("/");
		Path bundlePath = Paths.get(bundleFileUrl.toURI());
		ISourcePathResolver resolver = new ResourceResolver(Collections.singletonList(bundlePath));
		URI bundleUri = URI.create("/bundle-reference-models/dependent/bundle.xml");
		IBundle plain = loader.getBundle(bundleUri, resolver);
		IBundle bundle = testee.expandedBundle(plain, resolver);
		
		assertNotNull("bundle", bundle);
		assertEquals("baseUri", bundleUri, bundle.getUri());
		assertEquals("defaultLocale", Locale.GERMANY, bundle.getDefaultLocale());
		assertEquals("supportedLocales count", 3, bundle.getSupportedLocales().size());
		assertEquals("output count", 2, bundle.getOutputMapping().size());
		assertTrue("output-mapping HTML", bundle.getOutputMapping().containsKey(Format.HTML));
		assertTrue("output-mapping PDF", bundle.getOutputMapping().containsKey(Format.PDF));
		
		final IOutputConfig output = bundle.getOutputMapping().get(Format.HTML);
		
		assertEquals("HTML output format", Format.HTML, output.getFormat());
		
		assertUri("HTML module's first template", "/bundle-reference-models/base/transformations/html/Template1.xsl", getFirst(output.getModule().getTemplates(), null));
		assertUri("HTML module's last template", "/bundle-reference-models/dependent/transformations/html/Template3.xsl", getLast(output.getModule().getTemplates()));
		assertUri("HTML module's first style", "/bundle-reference-models/base/css/style1.scss", getFirst(output.getModule().getStyles(), null));
		assertUri("HTML module's last style", "/bundle-reference-models/dependent/css/style3.scss", getLast(output.getModule().getStyles()));
		assertUri("HTML module's first script", "/bundle-reference-models/base/js/script1.js", getFirst(output.getModule().getScripts(), null));
		assertUri("HTML module's last script", "/bundle-reference-models/dependent/js/script3.js", getLast(output.getModule().getScripts()));
		
		assertUri("HTML theme's base", "/bundle-reference-models/base/bundle.xml", output.getTheme().getBaseTheme());
		assertUri("HTML theme's first template", "/bundle-reference-models/base/theme/transformations/html/Template1.xsl", getFirst(output.getTheme().getTemplates(), null));
		assertUri("HTML theme's last template", "/bundle-reference-models/dependent/theme/transformations/html/Template3.xsl", getLast(output.getTheme().getTemplates()));
		assertUri("HTML theme's first style", "/bundle-reference-models/base/theme/css/style1.scss", getFirst(output.getTheme().getStyles(), null));
		assertUri("HTML theme's last style", "/bundle-reference-models/dependent/theme/css/style3.scss", getLast(output.getTheme().getStyles()));
		assertUri("HTML theme's first script", "/bundle-reference-models/base/theme/js/script1.js", getFirst(output.getTheme().getScripts(), null));
		assertUri("HTML theme's last script", "/bundle-reference-models/dependent/theme/js/script3.js", getLast(output.getTheme().getScripts()));
	}

	private void assertUri(String msg, String expected, URI uri) {
		assertEquals(msg, URI.create(expected), uri);
	}
}
