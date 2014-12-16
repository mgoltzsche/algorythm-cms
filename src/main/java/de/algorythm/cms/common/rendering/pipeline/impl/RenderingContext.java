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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;
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
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISchemaLocation;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;
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
	
	static private final class TemplateDefinitionErrorListener implements ErrorListener {
		
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
			
			sb.append(error);
		}
		
		return sb.toString();
	}
	
	private final IBundle bundle;
	private final URI resourcePrefix;
	private final Path outputDirectory;
	private final Path tempDirectory;
	private final IUriResolver resourceResolver;
	private final IOutputUriResolver outputResolver;
	private final IOutputUriResolver tmpResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private SAXParserFactory saxParserFactory;
	private final IXmlLoader xmlLoader;

	public RenderingContext(final IBundle bundle, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
		this.resourceResolver = new ResourceResolver(bundle, tempDirectory);
		this.outputResolver = new OutputResolver(outputDirectory);
		this.tmpResolver = new OutputResolver(tempDirectory);
		final Set<ISchemaLocation> locations = new LinkedHashSet<ISchemaLocation>(bundle.getSchemaLocations());
		final List<URI> schemaLocations = new LinkedList<URI>();
		
		for (ISchemaLocation location : locations)
			schemaLocations.add(location.getUri());
		
		try {
			this.xmlLoader = new XmlDomLoader(schemaLocations, resourceResolver);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public RenderingContext(final IBundle bundle, final IXmlLoader xmlLoader, final IUriResolver uriResolver, final IOutputUriResolver outUriResolver, final IOutputUriResolver tmpOutUriResolver, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.xmlLoader = xmlLoader;
		this.resourceResolver = uriResolver;
		this.outputResolver = outUriResolver;
		this.tmpResolver = tmpOutUriResolver;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
	}

	@Override
	public IBundleRenderingContext createLocalized(final Locale locale, boolean localizeOutput) {
		final IUriResolver uriResolver = resourceResolver.createLocalizedResolver(locale);
		final IOutputUriResolver tmpOutUriResolver = tmpResolver.createLocalizedResolver(locale);
		final IOutputUriResolver outUriResolver = localizeOutput
				? outputResolver.createLocalizedResolver(locale)
				: outputResolver;
		
		return new RenderingContext(bundle, xmlLoader, uriResolver, outUriResolver, tmpOutUriResolver, tempDirectory, outputDirectory, resourcePrefix);
	}
	
	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public XMLReader createXmlReader() {
		try {
			final XMLReader r = saxParserFactory.newSAXParser().getXMLReader();
			
			r.setErrorHandler(ERROR_HANDLER);
			
			return r;
		} catch(Exception e) {
			throw new IllegalStateException("Cannot create SAX parser. " + e, e);
		}
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
	public IUriResolver getResourceResolver() {
		return resourceResolver;
	}

	@Override
	public IOutputUriResolver getOutputResolver() {
		return outputResolver;
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
	public Document getDocument(URI uri) {
		final Path path = resourceResolver.resolve(uri);
		
		return xmlLoader.getDocument(path);
	}

	@Override
	public Templates compileTemplates(final Collection<URI> xslSourceUris) {
		final Source xslSource = createMergedXslSource(xslSourceUris);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final TemplateDefinitionErrorListener errorListener = new TemplateDefinitionErrorListener();
		final Templates templates;
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(new CmsTemplateURIResolver(resourceResolver));
		
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
	public Transformer createTransformer(final Templates templates, final URI notFoundContent) throws TransformerConfigurationException {
		final Transformer transformer = templates.newTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final URIResolver uriResolverAdapter = new CmsInputURIResolver(this, notFoundContent);
		final OutputURIResolver outputUriResolverAdapter = new CmsOutputURIResolver(tmpResolver, outputResolver);
		
		transformer.setURIResolver(uriResolverAdapter);
		trnsfrmCtrl.setOutputURIResolver(outputUriResolverAdapter);
		
		return transformer;
	}

	@Override
	public void transform(URI sourceUri, URI targetUri, Transformer transformer) throws IOException, TransformerException {
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final Node sourceNode = getDocument(sourceUri);
		final DOMSource source = new DOMSource(sourceNode, sourceUri.toString());
		final String targetUriScheme = targetUri.getScheme();
		final boolean tmp = targetUriScheme != null && "tmp".equals(targetUriScheme.toLowerCase());
		final IOutputUriResolver outUriResolver = tmp
				? tmpResolver : outputResolver;
		final Path outputFile = outUriResolver.resolveUri(targetUri);
		Files.createDirectories(outputFile.getParent());
		final Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
		final StreamResult result = new StreamResult(writer);
		final String targetUriStr = targetUri.toString();
		final TemplateDefinitionErrorListener errorListener = new TemplateDefinitionErrorListener();
		
		result.setSystemId(targetUriStr);
		trnsfrmCtrl.setBaseOutputURI(targetUriStr);
		transformer.setErrorListener(errorListener);
		final long startTime = new Date().getTime();
		transformer.transform(source, result);
		System.out.println("transform " + sourceUri + "  " + (new Date().getTime() - startTime));
		
		errorListener.evaluateErrors();
	}

	private Source createMergedXslSource(final Collection<URI> xslSourceUris) {
		if (xslSourceUris.size() == 1) {
			final URI sourceUri = xslSourceUris.iterator().next();
			final Path sourcePath = resourceResolver.resolve(sourceUri);
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