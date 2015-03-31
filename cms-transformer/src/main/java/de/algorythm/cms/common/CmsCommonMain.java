package de.algorythm.cms.common;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Module;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.impl.FileOutputTargetFactory;

public class CmsCommonMain {

	static private final Logger log = LoggerFactory.getLogger(CmsCommonMain.class);
	
	static public void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Invalid usage. Parameters: ROOT_DIRECTORIES BUNDLE_URI OUTPUT_DIRECTORY");
			System.exit(1);
		}
		
		final String[] rootDirs = args[0].split(":");
		final URI bundleUri = URI.create(args[1]);
		final Path outputDirectory = Paths.get(args[2]);
		final List<Path> rootPathes = new LinkedList<>();
		
		for (String rootDir : rootDirs) {
			final Path rootPath = Paths.get(rootDir);
			
			if (!Files.exists(rootPath)) {
				System.err.println("Resource root path " + rootPath + " does not exist");
				System.exit(2);
			}
			
			rootPathes.add(rootPath);
		}
		
		try {
			final Module module = new CmsCommonModule();
			final CmsCommonMain main = new CmsCommonMain(module);
			
			main.generate(bundleUri, rootPathes, outputDirectory);
			main.shutdown();
		} catch(Throwable e) {
			log.error("XML transformation failed", e);
			System.exit(3);
		}
	}


	@Inject
	private ICmsCommonFacade facade;

	public CmsCommonMain(final Module module) {
		final TimeMeter meter = TimeMeter.meter("Framework initialization");
		Guice.createInjector(module).injectMembers(this);
		meter.finish();
	}

	public void generate(final URI bundleUri, final List<Path> resourceRootPathes, final Path outputDirectory) throws Throwable {
		final IInputResolver resolver = facade.createInputResolver(resourceRootPathes);
		final IBundle bundle = facade.loadBundle(bundleUri, resolver);
		final IRenderer renderer = facade.createRenderer(bundle, resolver);
		
		renderer.renderAll(Format.HTML, new FileOutputTargetFactory(outputDirectory));
	}

	public void shutdown() {
		facade.shutdown();
	}
}
