package de.algorythm.cms.common;

import java.io.File;

import org.junit.Test;

public class TestMain {

	@Test
	public void testMain() throws Exception {
		File bundleXml = new File(getClass().getResource("/test-repo/example1.org/bundle.xml").toURI());
		File outputDir = new Configuration().outputDirectory;
		
		new CmsCommonMain(new CmsCommonModule()).generate(bundleXml, outputDir);
	}
}
