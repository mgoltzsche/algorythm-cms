package de.algorythm.cms.common;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class TestMain {

	@Test
	public void testMain() throws Throwable {
		Path bundleXml = Paths.get(getClass().getResource("/test-repo/example1.org/bundle.xml").toURI());
		Path outputDir = new Configuration().outputDirectory;
		
		new CmsCommonMain(new CmsCommonModule()).generate(bundleXml, outputDir);
	}
}
