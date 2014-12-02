package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class JavascriptCompressor implements IRenderingJob {

	static private final URI MAIN_JS_URI = URI.create("/main.js");
	
	private final Set<File> sources = new LinkedHashSet<File>();

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final File mainJsFile = new File(ctx.getOutputUriResolver().resolveResourceUri(MAIN_JS_URI));
		final StringBuilder scripts = new StringBuilder();
		
		for (File source : sources)
			scripts.append(FileUtils.readFileToString(source, Charsets.UTF_8.name()));
		
		FileUtils.writeStringToFile(mainJsFile, scripts.toString(), Charsets.UTF_8.name());
	}
}