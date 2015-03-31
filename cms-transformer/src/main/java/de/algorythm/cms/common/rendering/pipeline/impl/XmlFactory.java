package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.rendering.pipeline.IXmlSourceResolverProvider;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.XmlSource;

@Singleton
public class XmlFactory implements IXmlFactory {

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

	private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
	private final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	private final IXmlSourceResolverProvider xmlSourceResolverProvider;
	private final JAXBContext jaxbContext;

	@Inject
	public XmlFactory(final JAXBContext jaxbContext, IXmlSourceResolverProvider xmlSourceResolverProvider) throws Exception {
		this.jaxbContext = jaxbContext;
		this.xmlSourceResolverProvider = xmlSourceResolverProvider;
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
	public void parse(final URI publicUri, final ContentHandler handler, final IRenderingContext ctx) throws IOException, SAXException, ParserConfigurationException, ResourceNotFoundException {
		final InputSource source = createInputSource(publicUri, ctx);
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		
		source.setSystemId(publicUri.toString());
		reader.setErrorHandler(ERROR_HANDLER);
		reader.setContentHandler(handler);
		reader.parse(source);
	}
	
	private InputSource createInputSource(final URI publicUri, final IRenderingContext ctx) throws ResourceNotFoundException, IOException {
		final InputStream stream = ctx.createInputStream(publicUri);
		final InputSource source = new InputSource(stream);
		
		source.setSystemId(publicUri.toString());
		
		return source;
	}
	
	@Override
	public Templates compileTemplates(final Collection<URI> xslSourceUris, final IRenderingContext ctx) throws TransformerConfigurationException {
		return compileTemplates(createMergedXslSource(xslSourceUris), ctx);
	}
	
	@Override
	public Templates compileTemplates(final URI xslSourceUri, final IRenderingContext ctx) throws TransformerConfigurationException, ResourceNotFoundException, IOException {
		try (InputStream stream = ctx.createInputStream(xslSourceUri)) {
			return compileTemplates(new XmlSource(xslSourceUri, stream), ctx);
		}
	}
	
	/*@Override
	public ContentHandler createXMLWriter(final OutputStream out) throws TransformerConfigurationException {
		final TransformerHandler handler = transformerFactory.newTransformerHandler();
		
		handler.setResult(new StreamResult(out));
		
		return handler;
	}
	
	@Override
	public XMLFilter createXMLFilter(final Templates templates, final XMLReader parent, final URIResolver resolver) throws TransformerConfigurationException {
		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		
		transformerFactory.setErrorListener(ERROR_LISTENER);
		transformerFactory.setURIResolver(resolver);
		
		final XMLFilter filter = transformerFactory.newXMLFilter(templates);
		
		filter.setParent(parent);
		
		return filter;
		
	}*/
	
	private Templates compileTemplates(final Source xslSource, final IRenderingContext ctx) throws TransformerConfigurationException {
		final TimeMeter meter = TimeMeter.meter("template compilation");
		//final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		final TemplateErrorListener errorListener = new TemplateErrorListener(ctx.getName());
		final Templates templates;
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(new ContextAwareURIResolver(ctx, xmlSourceResolverProvider.getXmlSourceResolver()));
		
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
	public TransformerHandler createTransformerHandler(final Templates templates, final IRenderingContext ctx, final String outputPath, final IOutputTargetFactory targetFactory) throws IOException, TransformerConfigurationException {
		final TransformerHandler transformerHandler = createTransformerHandler(templates, targetFactory, ctx.getTmpResources());
		final IOutputTarget target = targetFactory.createOutputTarget(outputPath);
		final Result result = new StreamResult(target.createOutputStream());
		
		result.setSystemId(outputPath);
		//trnsfrmCtrl.setBaseOutputURI(outputUriStr);
		transformerHandler.setResult(result);
		
		return transformerHandler;
	}

	private TransformerHandler createTransformerHandler(final Templates templates, final IOutputTargetFactory out, final IWriteableResources tmp) throws IOException, TransformerConfigurationException {
		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(templates);
		final Transformer transformer = transformerHandler.getTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final OutputURIResolver outputUriResolverAdapter = new CmsOutputURIResolver(out, tmp);
		
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

	private Schema createSchema(final Collection<URI> schemaLocationUris, final IInputResolver inputResolver) throws Exception {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocationUris.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new CmsSchemaResolver(inputResolver));
		
		for (URI schemaLocationUri : schemaLocationUris) {
			final InputStream stream = inputResolver.createInputStream(schemaLocationUri);
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
	public Source createXmlSource(URI uri, IRenderingContext ctx)
			throws ResourceNotFoundException, IOException {
		return xmlSourceResolverProvider.getXmlSourceResolver().createXmlSource(uri, ctx);
	}
	
	@Override
	public Marshaller createMarshaller() throws JAXBException {
		return jaxbContext.createMarshaller();
	}

	@Override
	public Unmarshaller createUnmarshaller() throws JAXBException {
		return jaxbContext.createUnmarshaller();
	}
}
