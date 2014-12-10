package de.algorythm.cms.common.rendering.pipeline.job;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;

public class JavascriptCompressor implements IRenderingJob {

	static private final Path MAIN_JS = Paths.get("main.js");
	
	private final Set<Path> sources = new LinkedHashSet<Path>();

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final IUriResolver inResolver = ctx.getResourceResolver();
		final IOutputUriResolver outResolver = ctx.getOutputResolver();
		final Path jsPath = ctx.getResourcePrefix().resolve(MAIN_JS);
		final Path jsSystemPath = outResolver.resolveUri(jsPath);
		final LinkedList<String> scriptLines = new LinkedList<String>();
		
		for (Path source : sources) {
			final Path file = inResolver.resolve(source);
			
			for (String line : Files.readAllLines(file, StandardCharsets.UTF_8))
				scriptLines.add(line);
		}
		
		Files.createDirectories(jsSystemPath.getParent());
		Files.write(jsSystemPath, scriptLines, StandardCharsets.UTF_8);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}