package de.algorythm.cms.common;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Module;

import de.algorythm.cms.common.model.entity.IBundle;

public class CmsCommonMain {

	static private final Logger log = LoggerFactory.getLogger(CmsCommonMain.class);
	
	static public void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Invalid usage. Parameters: BUNDLE_FILE OUTPUT_DIRECTORY");
			System.exit(1);
		}
		
		final File bundleXmlFile = new File(args[0]);
		final File outputDirectory = new File(args[1]);
		
		if (!bundleXmlFile.exists()) {
			System.err.println("Invalid parameter value: " + bundleXmlFile + " not found");
			System.exit(2);
		}
		
		if (outputDirectory.isFile()) {
			System.err.println("Invalid parameter value: " + outputDirectory + " is a file");
			System.exit(2);
		}
		
		try {
			final Module module = new CmsCommonModule();
			final CmsCommonMain main = new CmsCommonMain(module);
			
			main.generate(bundleXmlFile, outputDirectory);
		} catch(Throwable e) {
			log.error("XML transformation failed", e);
			System.exit(3);
		}
	}
	

	@Inject
	private ICmsCommonFacade facade;
	
	public CmsCommonMain(final Module module) {
		Guice.createInjector(module).injectMembers(this);
	}
	
	public void generate(final File bundleXml, final File outputDirectory) throws IOException {
		final IBundle bundle = facade.loadBundle(bundleXml);
		final String tmpDirName = "algorythm-cms-" + new Date().getTime();
		final File tmpDirectory = new File(System.getProperty("java.io.tmpdir", null), tmpDirName);
		
		if (!tmpDirectory.mkdir())
			throw new IOException("Cannot create temp directory " + tmpDirectory);
		
		if (outputDirectory.exists())
			FileUtils.deleteDirectory(outputDirectory);
		
		if (!outputDirectory.mkdirs())
			throw new IOException("Cannot create output directory " + outputDirectory);
		
		facade.generatePagesXml(bundle, tmpDirectory);
		facade.generateSite(bundle, tmpDirectory, outputDirectory);
	}
}
