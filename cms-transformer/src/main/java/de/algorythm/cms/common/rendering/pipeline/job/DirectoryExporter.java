/*package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.impl.SimpleFileVisitor;

@Singleton
public class DirectoryExporter {

	static private final Logger log = LoggerFactory.getLogger(DirectoryExporter.class);
	static private final int BUFFER_SIZE = 8192;

	public void exportDirectories(final IRenderingContext ctx, final List<URI> directories, final IOutputTargetFactory targetFactory) throws Exception {
		final Path rootDir = ctx.createInputStream(URI.create("/"));
		
		for (URI dirURI : directories) {
			Path dirPath = ctx.createInputStream(dirURI);

			if (!Files.exists(dirPath))
				log.warn("Cannot export directory " + dirPath + " because it does not exist");

			Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					final String path = rootDir.relativize(file).toString();
					final IOutputTarget target = targetFactory.createOutputTarget(path);

					try (InputStream in = Files.newInputStream(file);
							OutputStream out = target.createOutputStream()) {
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
}*/
