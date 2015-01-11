package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.sf.saxon.Controller;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlContext;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.XmlSource;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;

public class XmlContext implements IXmlContext, URIResolver {

	static private final Logger log = LoggerFactory.getLogger(XmlContext.class);
	
	static final private ErrorHandler ERROR_HANDLER = new ErrorHandler() {
		@Override
		public void warning(SAXParseException exception) throws SAXException {
			throw exception;
		}
		
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}
		
		@Override
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}
	};
	
	static private final ErrorListener ERROR_LISTENER = new ErrorListener() {
		@Override
		public void warning(TransformerException exception)
				throws TransformerException {
			log.warn("Transformation warning", exception);
		}
		
		@Override
		public void fatalError(TransformerException exception)
				throws TransformerException {
			throw exception;
		}
		
		@Override
		public void error(TransformerException exception)
				throws TransformerException {
			throw exception;
		}
	};

	private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
	private final SAXTransformerFactory writerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	private final IBundleRenderingContext ctx;
	
	public XmlContext(final IBundleRenderingContext ctx) throws Exception {
		this.ctx = ctx;
		//final Schema schema = createSchema(schemaLocationUris, sourceUriResolver);
		
		//factory.setSchema(schema);
		/*parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(false);
		parserFactory.setXIncludeAware(false);*/
	}

	@Override
	public XMLEventReader createXMLEventReader(final InputStream stream) throws XMLStreamException {
		return xmlInputFactory.createXMLEventReader(stream);
	}

	@Override
	public XMLReader createXMLReader() throws SAXException {
		return XMLReaderFactory.createXMLReader();
	}
	
	@Override
	public ContentHandler createXMLWriter(final URI publicUri) throws IOException, TransformerConfigurationException {
		final Path outputFile = ctx.resolveDestination(publicUri);
		Files.createDirectories(outputFile.getParent());
		final OutputStream outputStream = Files.newOutputStream(outputFile);
		final TransformerHandler handler = writerFactory.newTransformerHandler();
		
		handler.setResult(new StreamResult(outputStream));
		
		return handler;
	}
	
	@Override
	public void parse(final URI publicUri, final ContentHandler handler) throws IOException, SAXException, ParserConfigurationException, ResourceNotFoundException {
		final InputSource source = createInputSource(publicUri);
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		
		source.setSystemId(publicUri.toString());
		reader.setErrorHandler(ERROR_HANDLER);
		reader.setContentHandler(handler);
		reader.parse(source);
	}
	
	private InputSource createInputSource(final URI publicUri) throws IOException, ResourceNotFoundException {
		final Path path = ctx.resolveSource(publicUri);
		final InputStream stream = Files.newInputStream(path);
		final InputSource source = new InputSource(stream);
		
		source.setSystemId(publicUri.toString());
		
		return source;
	}
	
	@Override
	public Templates compileTemplates(final Collection<URI> xslSourceUris) throws TransformerConfigurationException {
		return compileTemplates(createMergedXslSource(xslSourceUris));
	}
	
	@Override
	public Templates compileTemplates(final URI xslSourceUri) throws TransformerConfigurationException, ResourceNotFoundException {
		final Path path = ctx.resolveSource(xslSourceUri);
		
		if (path == null)
			throw new TransformerConfigurationException("Cannot resolve " + xslSourceUri);
		
		try {
			return compileTemplates(new XmlSource(xslSourceUri, path));
		} catch (IOException e) {
			throw new TransformerConfigurationException(e);
		}
	}
	
	private Templates compileTemplates(final Source xslSource) throws TransformerConfigurationException {
		final TimeMeter meter = TimeMeter.meter("template compilation");
		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		final Templates templates;
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(this);
		
		try {
			templates = transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			throw new TransformerConfigurationException("Cannot load XSL templates. " + errorListener, e);
		}
		
		errorListener.evaluateErrors();
		meter.finish();
		
		return templates;
	}

	@Override
	public XMLFilter createXMLFilter(final Templates templates, final XMLReader parent) throws TransformerConfigurationException {
		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		
		transformerFactory.setErrorListener(ERROR_LISTENER);
		transformerFactory.setURIResolver(this);
		
		final XMLFilter filter = transformerFactory.newXMLFilter(templates);
		
		filter.setParent(parent);
		
		return filter;
		
	}
	@Override
	public TransformerHandler createTransformerHandler(final Templates templates, final URI outputUri) throws IOException, TransformerConfigurationException {
		final TransformerHandler transformerHandler = createTransformerHandler(templates);
		final Path outputFile = ctx.resolveDestination(outputUri);
		Files.createDirectories(outputFile.getParent());
		final OutputStream outputStream = Files.newOutputStream(outputFile);
		final Result result = new StreamResult(outputStream);
		final String outputUriStr = outputUri.toString();
		
		result.setSystemId(outputUriStr);
		//trnsfrmCtrl.setBaseOutputURI(outputUriStr);
		transformerHandler.setResult(result);
		
		return transformerHandler;
	}

	private TransformerHandler createTransformerHandler(final Templates templates) throws IOException, TransformerConfigurationException {
		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(templates);
		final Transformer transformer = transformerHandler.getTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final OutputURIResolver outputUriResolverAdapter = new CmsOutputURIResolver(ctx);
		
		trnsfrmCtrl.setOutputURIResolver(outputUriResolverAdapter);
		
		return transformerHandler;
	}

	private Source createMergedXslSource(final Collection<URI> xslSourceUris) {
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (URI sourceUri : xslSourceUris) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(sourceUri.toString()))
				.append("\" />");
		}
		
		xslt.append("</xsl:stylesheet>");
		
		return new StreamSource(new StringReader(xslt.toString()));
	}

	private Schema createSchema(final Collection<URI> schemaLocationUris, final ISourcePathResolver sourcePathResolver) throws Exception {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocationUris.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new CmsSchemaResolver(sourcePathResolver));
		
		for (URI schemaLocationUri : schemaLocationUris) {
			final Path schemaLocation = sourcePathResolver.resolveSource(schemaLocationUri);
			final InputStream stream = Files.newInputStream(schemaLocation);
			final Source source = new StreamSource(stream);
			sources[i++] = source;
			
			source.setSystemId(schemaLocationUri.toString());
		}
		
		try {
			return schemaFactory.newSchema(sources);
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML schema. " + e, e);
		}
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI uri = href.isEmpty() ? baseUri : baseUri.resolve(href);
		
		try {
			return ctx.createXmlSource(uri);
		} catch(ResourceNotFoundException | IOException e) {
			throw new TransformerException(e);
		}
	}
}
