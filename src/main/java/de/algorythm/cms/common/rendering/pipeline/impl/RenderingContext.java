package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISchemaLocation;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsOutputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsTemplateURIResolver;
import de.algorythm.cms.common.resources.impl.OutputResolver;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class RenderingContext implements IBundleRenderingContext {

	static private final Logger log = LoggerFactory.getLogger(RenderingContext.class);
	
	static public ErrorHandler ERROR_HANDLER = new ErrorHandler() {

		@Override
		public void warning(final SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void error(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}

		@Override
		public void fatalError(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}
	};
	
	static private final class TemplateErrorListener implements ErrorListener {
		
		private List<TransformerException> warnings = new LinkedList<TransformerException>();
		private List<TransformerException> errors = new LinkedList<TransformerException>();
		
		@Override
		public void warning(TransformerException exception)
				throws TransformerException {
			warnings.add(exception);
		}
		
		@Override
		public void fatalError(TransformerException exception)
				throws TransformerException {
			errors.add(exception);
		}
		
		@Override
		public void error(TransformerException exception)
				throws TransformerException {
			errors.add(exception);
		}
		
		public void evaluateErrors() {
			if (!errors.isEmpty()) {
				final String msg = errorsToString("Errors:", errors);
				
				throw new IllegalStateException("Cannot load XSL templates: " + msg);
			} else if (!warnings.isEmpty()) {
				final String msg = errorsToString("Warnings:", warnings);
				
				log.warn(msg);
			}
		}
	};
	
	static private String errorsToString(final String label, final Iterable<TransformerException> errors) {
		final StringBuilder sb = new StringBuilder(label);
		
		for (TransformerException error : errors) {
			SourceLocator l = error.getLocator();
			
			if (l != null) {
				sb.append("\n\t").append(l.getSystemId()).append(':')
					.append(l.getLineNumber()).append(':')
					.append(l.getColumnNumber()).append(" - ");
			}
			
			sb.append(error.getCause());
		}
		
		return sb.toString();
	}
	
	private final IBundle bundle;
	private final URI resourcePrefix;
	private final Path outputDirectory;
	private final Path tempDirectory;
	private final ISourceUriResolver sourceResolver;
	private final ITargetUriResolver targetResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private final IXmlLoader xmlLoader;
	private final Map<Locale, IPageConfig> localizedPages;

	public RenderingContext(final IBundle bundle, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
		this.sourceResolver = new ResourceResolver(bundle, tempDirectory);
		this.targetResolver = new OutputResolver(outputDirectory, tempDirectory);
		localizedPages = new HashMap<Locale, IPageConfig>();
		final Set<ISchemaLocation> locations = new LinkedHashSet<ISchemaLocation>(bundle.getSchemaLocations());
		final List<URI> schemaLocations = new LinkedList<URI>();
		
		for (ISchemaLocation location : locations)
			schemaLocations.add(location.getUri());
		
		try {
			this.xmlLoader = new XmlDomLoader(schemaLocations, sourceResolver);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public IPageConfig getStartPage(Locale locale) {
		return localizedPages.get(locale);
	}

	@Override
	public void setStartPage(final Locale locale, final IPageConfig startPage) {
		localizedPages.put(locale, startPage);
	}

	@Override
	public IXmlLoader getXmlLoader() {
		return xmlLoader;
	}

	@Override
	public Path getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public Path getTempDirectory() {
		return tempDirectory;
	}

	@Override
	public ISourceUriResolver getResourceResolver() {
		return sourceResolver;
	}

	@Override
	public ITargetUriResolver getOutputResolver() {
		return targetResolver;
	}

	@Override
	public URI getResourcePrefix() {
		return resourcePrefix;
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public Document getDocument(URI uri, Locale locale) {
		final Path path = sourceResolver.resolve(uri, locale);
		
		return xmlLoader.getDocument(path);
	}

	@Override
	public Templates compileTemplates(final Collection<URI> xslSourceUris) {
		final Source xslSource = createMergedXslSource(xslSourceUris);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		final Templates templates;
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(new CmsTemplateURIResolver(sourceResolver));
		
		try {
			templates = transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			final String msg = errorsToString("Errors:", errorListener.errors);
			
			throw new IllegalStateException("Cannot load XSL templates. " + msg, e);
		}
		
		errorListener.evaluateErrors();
		
		return templates;
	}

	@Override
	public Transformer createTransformer(final Templates templates, final URI notFoundContent, final Locale locale) throws TransformerConfigurationException {
		final Transformer transformer = templates.newTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final URIResolver uriResolverAdapter = new CmsInputURIResolver(this, locale, notFoundContent);
		final OutputURIResolver outputUriResolverAdapter = new CmsOutputURIResolver(targetResolver, locale);
		
		transformer.setURIResolver(uriResolverAdapter);
		trnsfrmCtrl.setOutputURIResolver(outputUriResolverAdapter);
		
		return transformer;
	}

	@Override
	public void transform(URI sourceUri, URI targetUri, Transformer transformer, Locale locale) throws IOException, TransformerException {
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final Node sourceNode = getDocument(sourceUri, locale);
		final DOMSource source = new DOMSource(sourceNode, sourceUri.toString());
		final Path outputFile = targetResolver.resolveUri(targetUri, locale);
		Files.createDirectories(outputFile.getParent());
		final Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
		final StreamResult result = new StreamResult(writer);
		final String targetUriStr = targetUri.toString();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		
		result.setSystemId(targetUriStr);
		trnsfrmCtrl.setBaseOutputURI(targetUriStr);
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		errorListener.evaluateErrors();
	}

	private Source createMergedXslSource(final Collection<URI> xslSourceUris) {
		if (xslSourceUris.size() == 1) {
			final URI sourceUri = xslSourceUris.iterator().next();
			final Path sourcePath = sourceResolver.resolve(sourceUri, Locale.ROOT);
			final InputStream stream;
			
			try {
				stream = Files.newInputStream(sourcePath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			final Source source = new StreamSource(stream, sourceUri.toString());
			
			return source;
		}
		
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (URI sourceUri : xslSourceUris) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(sourceUri.toString()))
				.append("\" />");
		}
		
		final String mergedXsl = xslt.append("</xsl:stylesheet>").toString();
		final Reader mergedTplReader = new StringReader(mergedXsl);
		final StreamSource source = new StreamSource(mergedTplReader);
		
		return source;
	}
}