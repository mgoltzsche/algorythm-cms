package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.impl.SimpleFileVisitor;

public class DirectoryExporter implements IRenderingJob {

	static private final Logger log = LoggerFactory.getLogger(DirectoryExporter.class);
	static private final int BUFFER_SIZE = 8192;

	private final List<URI> directories = new LinkedList<URI>();

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		for (URI dirURI : directories) {
			final String path = dirURI.getPath().substring(1);
			final Path inDir = ctx.getBundle().getLocation().resolve(path).normalize();
			final int inDirLength = inDir.toUri().getPath().length() - path.length() - 1;

			if (!Files.exists(inDir))
				log.warn("Cannot export directory " + inDir + " because it does not exist");

			Files.walkFileTree(inDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					final String path = file.toUri().getPath().substring(inDirLength);

					try (InputStream in = Files.newInputStream(file);
							OutputStream out = ctx.createOutputStream(path)) {
						copy(in, out);
					} catch (IOException e) {
						throw e;
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	private long copy(InputStream source, OutputStream sink) throws IOException {
		long nread = 0L;
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = source.read(buf)) > 0) {
			sink.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}
}
