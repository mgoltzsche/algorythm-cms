package de.algorythm.maven.webResourcesPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.w3c.css.sac.InputSource;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.resolver.ScssStylesheetResolver;

public class ScssResolver implements ScssStylesheetResolver {

	private final String encoding;

	public ScssResolver(final String encoding) {
		this.encoding = encoding;
	}

	@Override
	public InputSource resolve(final ScssStylesheet parentStylesheet, final String identifier) {
		final Path base = Paths.get(parentStylesheet.getFileName());
		final Path file = base.resolveSibling(identifier);
		
		try {
			final InputStream in = Files.newInputStream(file);
			final Reader reader = new InputStreamReader(in);
			final InputSource source = new InputSource(reader);
			
			source.setEncoding(encoding);
			source.setURI(file.toString());
			
			return source;
		} catch(IOException e) {
			throw new RuntimeException("Cannot read " + file.normalize(), e);
		}
	}
}
