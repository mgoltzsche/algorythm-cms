package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.inject.Singleton;

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
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class ScssCompiler {

	static private final URI MAIN_CSS = URI.create("main.css");

	private boolean compress = false;

	public void compileScss(final IRenderingContext ctx, final Collection<URI> sources, final IOutputTargetFactory targetFactory) throws Exception {
		final TimeMeter meter = TimeMeter.meter(this.toString());
		final String scss = createIncludingSCSS(sources);
		
		compileSource(scss, ctx, targetFactory);
		meter.finish();
	}
	
	private String createIncludingSCSS(final Collection<URI> uris) {
		final StringBuilder scss = new StringBuilder();
		
		for (URI uri : uris)
			scss.append("@import \"").append(StringEscapeUtils.escapeJava(uri.getPath())).append("\";\n");
		
		return scss.toString();
	}
	
	private void compileSource(final String scss, final IRenderingContext ctx, final IOutputTargetFactory targetFactory) throws Exception {
		final String cssPath = ctx.getResourcePrefix().resolve(MAIN_CSS).getPath();
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
        stylesheet.setFile(new File(cssPath));
		stylesheet.compile();
		String css = stylesheet.printState();
		
		if (compress) {
			final StringReader cssReader = new StringReader(css);
			final CssCompressor compressor = new CssCompressor(cssReader);
			final StringWriter writer = new StringWriter();
			
			compressor.compress(writer, 0);
			
			css = writer.toString().replace("\n", "");
		}
		
		final IOutputTarget target = targetFactory.createOutputTarget(cssPath);
		
		try (OutputStream out = target.createOutputStream()) {
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
				final InputStream stream;
				
				try {
					stream = ctx.createInputStream(publicUri);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				if (stream == null)
					throw new IllegalStateException("Cannot find stylesheet at '" + publicUri + "' that was referred in '" + base + "'");
				
				final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
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
