package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ITargetUriResolver;

public class JavascriptCompressor implements IRenderingJob {

	static private final URI MAIN_JS = URI.create("main.js");
	
	private final Set<Path> sources = new LinkedHashSet<Path>();

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final ITargetUriResolver outResolver = ctx.getOutputResolver();
		final URI jsUri = ctx.getResourcePrefix().resolve(MAIN_JS);
		final Path jsSystemPath = outResolver.resolveUri(URI.create("../" + jsUri.getPath()), Locale.ROOT);
		final LinkedList<String> scriptLines = new LinkedList<String>();
		
		for (Path source : sources) {
			for (String line : Files.readAllLines(source, StandardCharsets.UTF_8))
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