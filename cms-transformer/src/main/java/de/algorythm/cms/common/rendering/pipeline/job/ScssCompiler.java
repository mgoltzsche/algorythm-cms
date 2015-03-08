package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.css.sac.InputSource;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.resolver.ScssStylesheetResolver;
import com.yahoo.platform.yui.compressor.CssCompressor;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class ScssCompiler implements IRenderingJob {

	static private final URI MAIN_CSS = URI.create("main.css");

	private List<URI> config = new LinkedList<URI>();
	private List<URI> sources = new LinkedList<URI>();
	private boolean compress = false;

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this);
		final List<URI> uris = new LinkedList<URI>(config);
		final StringBuilder scss = new StringBuilder();
		
		uris.addAll(sources);
		createIncludingSCSS(uris, scss);
		compileSource(scss.toString(), ctx);
		meter.finish();
	}
	
	private void createIncludingSCSS(final List<URI> uris, final StringBuilder scss) {
		for (URI uri : uris)
			scss.append("@import \"").append(StringEscapeUtils.escapeJava(uri.getPath())).append("\";\n");
	}
	
	private void compileSource(final String scss, final IRenderingContext ctx) throws Exception {
		final URI cssUri = ctx.getResourcePrefix().resolve(MAIN_CSS);
		final SCSSDocumentHandler docHandler = new SCSSDocumentHandlerImpl();
		final SCSSErrorHandler errorHandler = new SCSSErrorHandler();
		final ScssStylesheet stylesheet = docHandler.getStyleSheet();
		final Parser parser = new Parser();
		final InputSource source = new InputSource(new StringReader(scss));
		
		parser.setErrorHandler(errorHandler);
		parser.setDocumentHandler(docHandler);
		parser.parseStyleSheet(source);
		stylesheet.addResolver(createResolver(ctx));
		stylesheet.setCharset(StandardCharsets.UTF_8.name());
        stylesheet.setFile(new File(cssUri.getPath()));
		stylesheet.compile();
		String css = stylesheet.printState();
		
		if (compress) {
			final StringReader cssReader = new StringReader(css);
			final CssCompressor compressor = new CssCompressor(cssReader);
			final StringWriter writer = new StringWriter();
			
			compressor.compress(writer, 0);
			
			css = writer.toString().replace("\n", "");
		}
		
		try (OutputStream out = ctx.createOutputStream(cssUri)) {
			out.write(css.getBytes(StandardCharsets.UTF_8));
		} catch(Exception e) {
			throw e;
		}
	}
	
	private ScssStylesheetResolver createResolver(final IRenderingContext ctx) {
		return new ScssStylesheetResolver() {
			@Override
			public InputSource resolve(final ScssStylesheet parentStylesheet, final String identifier) {
				final URI href = URI.create(identifier);
				final URI base = URI.create(parentStylesheet.getFileName());
				final URI publicUri = base.resolve(href);
				final Reader reader;
				
				try {
					final Path resolvedPath = ctx.resolveSource(publicUri);
					reader = Files.newBufferedReader(resolvedPath, StandardCharsets.UTF_8);
				} catch (ResourceNotFoundException | IOException e) {
					throw new RuntimeException(e);
				}
				
				final InputSource source = new InputSource(reader);
				
				source.setURI(publicUri.toString());
				
				return source;
			}
		};
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
