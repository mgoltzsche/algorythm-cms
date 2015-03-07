package de.algorythm.cms.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Module;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;

public class CmsCommonMain {

	static private final Logger log = LoggerFactory.getLogger(CmsCommonMain.class);
	
	static public void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Invalid usage. Parameters: BUNDLE_FILE OUTPUT_DIRECTORY");
			System.exit(1);
		}
		
		final Path bundleXmlFile = Paths.get(args[0]);
		final Path outputDirectory = Paths.get(args[1]);
		
		if (!Files.exists(bundleXmlFile)) {
			System.err.println("Invalid parameter value: " + bundleXmlFile + " not found");
			System.exit(2);
		}
		
		try {
			final Module module = new CmsCommonModule();
			final CmsCommonMain main = new CmsCommonMain(module);
			
			main.generate(bundleXmlFile, outputDirectory);
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
	
	public void generate(final Path bundleXml, final Path outputDirectory) throws Throwable {
		final IBundle bundle = facade.loadBundle(bundleXml);
		final IRenderer renderer = facade.createRenderer(bundle);
		
		renderer.render(outputDirectory).sync();
	}
	
	public void shutdown() {
		facade.shutdown();
	}
}
