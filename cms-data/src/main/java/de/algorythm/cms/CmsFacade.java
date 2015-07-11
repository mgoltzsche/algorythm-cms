package de.algorythm.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.basex.api.client.LocalSession;
import org.basex.api.client.Session;
import org.basex.core.Command;
import org.basex.core.Context;

public class CmsFacade implements AutoCloseable {

	static private final Set<String> IMPORT_EXTENSIONS = new HashSet<>(Arrays.asList(new String[] {"xml"}));
	final Session session;

	public CmsFacade() {
		Context context = new Context();
		session = new LocalSession(context, System.out);
	}

	@Override
	public void close() throws IOException {
		session.close();
	}

	public void openOrCreateDatabase(String name) throws IOException {
		session.execute("CHECK " + name);
	}

	public void openOrCreateDatabase(final String name, Path source) throws IOException {
		if (!Files.exists(source))
			throw new IllegalArgumentException("Database source file does not exist: " + source);

		if (Files.isDirectory(source)) {
			final URI sourceRootUri = source.toUri();

			openOrCreateDatabase(name);
			Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					final URI fileUri = file.toUri();
					final String dbPath = sourceRootUri.relativize(fileUri).getPath();
					final String extension = FilenameUtils.getExtension(dbPath);

					if ("xqm".equals(extension)) { // Import XQuery module
						// TODO: First send file to server if server runs on another machine
						session.execute("REPO INSTALL " + fileUri);
					} else if (IMPORT_EXTENSIONS.contains(extension)) { // Import selected content
						try (InputStream input = Files.newInputStream(file)) {
							session.replace(dbPath, input);
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			try (InputStream input = Files.newInputStream(source)) {
				session.create(name, input);
			}
		}

		session.execute("INFO STORAGE");
		session.execute("REPO LIST");
	}

	public void execute(Command command, IHandler<String> handler) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(512);

		try {
			session.setOutputStream(out);
			session.execute(command);
			handler.handle(out.toString(StandardCharsets.UTF_8.name()));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Replace method with more general method send(command, handler). Implement URL mapping inside cms-transformer module
	public void renderPage(String path, IHandler<String> handler) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(512);

		try {
			session.setOutputStream(out);
			session.execute("xquery doc('mydatabase" + path + "')");
			handler.handle(out.toString(StandardCharsets.UTF_8.name()));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
