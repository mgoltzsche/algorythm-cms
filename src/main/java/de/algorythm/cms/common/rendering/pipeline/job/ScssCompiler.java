package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

public class ScssCompiler implements IRenderingJob {

	static private final URI MAIN_CSS_URI = URI.create("/main.css");
	
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
		final URI outputFileUri = ctx.getOutputUriResolver().resolveResourceUri(MAIN_CSS_URI);
		final File cssFile = new File(outputFileUri);
		final SCSSDocumentHandler docHandler = new SCSSDocumentHandlerImpl();
		final SCSSErrorHandler errorHandler = new SCSSErrorHandler();
		final ScssStylesheet stylesheet = docHandler.getStyleSheet();
		
		stylesheet.addResolver(createResolver(ctx));
		
		final Parser parser = new Parser();
		parser.setErrorHandler(errorHandler);
		parser.setDocumentHandler(docHandler);
		
		final InputSource source = new InputSource(new StringReader(scss));
		parser.parseStyleSheet(source);
		stylesheet.addSourceUris(Arrays.asList(new String[] {ctx.getBundle().getName()}));
		stylesheet.setCharset(parser.getInputSource().getEncoding());
        stylesheet.setFile(new File(ctx.getBundle().getLocation()));
		stylesheet.compile();
		
		FileUtils.writeStringToFile(cssFile, stylesheet.printState());
	}
	
	private ScssStylesheetResolver createResolver(final IRenderingContext ctx) {
		return new ScssStylesheetResolver() {
			@Override
			public InputSource resolve(final ScssStylesheet parentStylesheet, final String identifier) {
				final URI publicHref = URI.create(identifier);
				final URI baseUri = URI.create(parentStylesheet.getFileName());
				final URI resolvedUri;
				
				try {
					resolvedUri = ctx.getInputUriResolver().toSystemUri(publicHref, baseUri);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
				
				return new InputSource(resolvedUri.toString());
			}
		};
	}
}
