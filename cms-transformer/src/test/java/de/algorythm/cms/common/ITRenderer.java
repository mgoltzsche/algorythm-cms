package de.algorythm.cms.common;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Guice;

import de.algorythm.cms.common.impl.DeadlockDetection;
import de.algorythm.cms.common.impl.DirectoryUtil;
import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.impl.FileOutputTargetFactory;

public class ITRenderer {

	@Inject
	public ICmsCommonFacade facade;
	
	@Test
	public void renderAll_should_terminate_without_error() throws Throwable {
		DeadlockDetection deadlockDetection = new DeadlockDetection();
		Path outputDirectory = Paths.get(getClass().getResource("/").toURI().resolve("test-output"));
		
		if (Files.exists(outputDirectory))
			DirectoryUtil.deleteDirectory(outputDirectory);
		
		IRenderer renderer = initRenderer();
		
		renderer.renderAll(Format.HTML, new FileOutputTargetFactory(outputDirectory));
		facade.shutdown();
		deadlockDetection.stop();
	}
	
	@Test
	public void renderArtifact_should_terminate_without_error() throws Throwable {
		DeadlockDetection deadlockDetection = new DeadlockDetection();
		IRenderer renderer = initRenderer();
		
		renderer.expand();
		TimeMeter meter = TimeMeter.meter("renderArtifact 1");
		renderer.renderArtifact(URI.create("/index.html"));
		meter.finish();
		meter = TimeMeter.meter("renderArtifact 2");
		String output = new String(renderer.renderArtifact(URI.create("/content.html")), StandardCharsets.UTF_8);
		meter.finish();
		System.out.println(output);
		facade.shutdown();
		deadlockDetection.stop();
	}
	
	private IRenderer initRenderer() throws Exception {
		URI repoUri = getClass().getResource("/integration-test-repo/").toURI();
		Path inputDirectory = Paths.get(repoUri);
		URI bundleUri = URI.create("/de/algorythm/bundle.xml");
		Guice.createInjector(new CmsCommonModule()).injectMembers(this);
		
		final IInputResolver resolver = facade.createInputResolver(inputDirectory);
		final IBundle bundle = facade.loadBundle(bundleUri, resolver);
		
		return facade.createRenderer(bundle, resolver);
	}
}