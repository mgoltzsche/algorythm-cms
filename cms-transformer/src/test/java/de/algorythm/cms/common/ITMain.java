package de.algorythm.cms.common;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.algorythm.cms.common.impl.DeadlockDetection;
import de.algorythm.cms.common.impl.DirectoryUtil;

public class ITMain {

	@Test
	public void main_should_terminate_without_error() throws Throwable {
		DeadlockDetection deadlockDetection = new DeadlockDetection();
		URI repoUri = getClass().getResource("/integration-test-repo/").toURI();
		Path repoDirectory = Paths.get(repoUri);
		Path outputDirectory = Paths.get(repoUri.resolve("../test-output"));
		URI bundleUri = URI.create("/de/algorythm/bundle.xml");
		//URI bundleUri = URI.create("/de/algorythm/cms/common/bundle.xml");
		List<Path> pathes = Collections.singletonList(repoDirectory);
		CmsCommonMain main = new CmsCommonMain(new CmsCommonModule());
		
		if (Files.exists(outputDirectory))
			DirectoryUtil.deleteDirectory(outputDirectory);
		
		main.generate(bundleUri, pathes, outputDirectory);
		main.shutdown();
		deadlockDetection.stop();
	}
}