package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IOutputUriResolver;

public class ScssCompiler implements IRenderingJob {

	static private final String MAIN_CSS = "main.css";
	
	private List<URI> config = new LinkedList<URI>();
	private List<URI> sources = new LinkedList<URI>();

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final List<URI> uris = new LinkedList<URI>(config);
		
		uris.addAll(sources);
		
		compileSource(createIncludingSCSS(uris), ctx);
	}
	
	private String createIncludingSCSS(final List<URI> uris) {
		final StringBuilder scss = new StringBuilder();
		
		for (URI uri : uris)
			scss.append("@import \"").append(StringEscapeUtils.escapeJava(uri.getPath())).append("\";\n");
		
		return scss.toString();
	}
	
	private void compileSource(final String scss, final IRenderingContext ctx) throws Exception {
		final IOutputUriResolver outResolver = ctx.getOutputResolver();
		final Path cssPath = ctx.getResourcePrefix().resolve(MAIN_CSS);
		final Path cssSystemPath = outResolver.resolveUri(cssPath);
		final SCSSDocumentHandler docHandler = new SCSSDocumentHandlerImpl();
		final SCSSErrorHandler errorHandler = new SCSSErrorHandler();
		final ScssStylesheet stylesheet = docHandler.getStyleSheet();
		final Parser parser = new Parser();
		final InputSource source = new InputSource(new StringReader(scss));
		
		parser.setErrorHandler(errorHandler);
		parser.setDocumentHandler(docHandler);
		parser.parseStyleSheet(source);
		stylesheet.addResolver(createResolver(ctx));
		stylesheet.setCharset(parser.getInputSource().getEncoding());
        stylesheet.setFile(cssSystemPath.toFile());
		stylesheet.compile();
		Files.createDirectories(cssSystemPath.getParent());
		Files.write(cssSystemPath, stylesheet.printState().getBytes(StandardCharsets.UTF_8));
	}
	
	private ScssStylesheetResolver createResolver(final IRenderingContext ctx) {
		return new ScssStylesheetResolver() {
			@Override
			public InputSource resolve(final ScssStylesheet parentStylesheet, final String identifier) {
				final Path publicPath = Paths.get(identifier);
				final Path basePath = Paths.get(parentStylesheet.getFileName());
				final Path resolvedUri = ctx.getResourceResolver().resolve(publicPath, basePath);
				
				return new InputSource(resolvedUri.toString());
			}
		};
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
