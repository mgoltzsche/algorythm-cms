package de.algorythm.cms.common;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.algorythm.cms.common.impl.DeadlockDetection;

public class TestMain {

	@Test
	public void testMain() throws Throwable {
		DeadlockDetection deadlockDetection = new DeadlockDetection();
		Path bundleXml = Paths.get(getClass().getResource("/test-repo/algorythm.de/bundle.xml").toURI());
		Path outputDir = new Configuration().outputDirectory;
		CmsCommonMain main = new CmsCommonMain(new CmsCommonModule());
		
		main.generate(bundleXml, outputDir);
		main.shutdown();
		deadlockDetection.stop();
	}
}
