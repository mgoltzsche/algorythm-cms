package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class ResourceAssembler implements IRenderingJob {

	private final List<String> pattern = new LinkedList<String>();
	
	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final File outputDirectory = ctx.getOutputDirectory();
		final List<Pattern> compiledPattern = new LinkedList<Pattern>();
		
		for (String p : pattern)
			compiledPattern.add(Pattern.compile(p));
		
		for (URI rootPath : ctx.getInputUriResolver().getRootPathes()) {
			final File rootDirectory = new File(rootPath);
			
			for (String resDirName : new String[] { "scripts", "img" }) {
				final File resDirectory = new File(rootDirectory, resDirName);
				final File outDirectory = new File(outputDirectory, resDirName);
				
				if (resDirectory.exists())
					FileUtils.copyDirectory(resDirectory, outDirectory);
			}
		}
	}
}
