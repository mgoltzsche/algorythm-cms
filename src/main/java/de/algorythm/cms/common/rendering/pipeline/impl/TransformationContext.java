package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.resources.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.impl.XsdResourceResolver;

public class TransformationContext {

	static private ErrorHandler ERROR_HANDLER = new ErrorHandler() {

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
	
	static private Templates createTransformationTemplates(final Collection<URI> xslSources, final IResourceResolver uriResolver) {
		final Source xslSource = createMergedXslSource(xslSources);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		transformerFactory.setURIResolver(new CmsInputURIResolver(uriResolver));
		
		try {
			return transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot load XSL templates. " + e, e);
		}
	}
	
	static private Source createMergedXslSource(final Collection<URI> xslSources) {
		if (xslSources.size() == 1)
			return new StreamSource(new File(xslSources.iterator().next()));
		
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (URI source : xslSources) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(source.getPath()))
				.append("\" />");
		}
		
		final String mergedXsl = xslt.append("</xsl:stylesheet>").toString();
		final Reader mergedTplReader = new StringReader(mergedXsl);
		
		return new StreamSource(mergedTplReader);
	}
	
	static private SAXParserFactory createSAXParserFactory(final Collection<URI> schemaLocations, final IResourceResolver uriResolver) throws FileNotFoundException {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final Schema schema = createSchema(schemaLocations, uriResolver);
		
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema); // REQUIRED FOR VALIDATION ONLY
		
		return parserFactory;
	}
	
	static private Schema createSchema(final Collection<URI> schemaLocations, final IResourceResolver uriResolver) throws FileNotFoundException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocations.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new XsdResourceResolver(uriResolver));
		
		for (URI schemaLocation : schemaLocations)
			sources[i++] = new StreamSource(new File(uriResolver.toSystemUri(schemaLocation)));
		
		try {
			return schemaFactory.newSchema(sources);
			//schema = schemaFactory.newSchema(new StreamSource(new File("/home/max/development/java/algorythm-cms/target/classes/de/algorythm/cms/common/types/CMS.xsd")));
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML schema. " + e, e);
		}
	}
	
	private final IRenderingContext processCtx;
	private final IResourceResolver resourceResolver;
	private final IOutputUriResolver outputUriResolver;
	private final SAXParserFactory parserFactory;
	private final Templates templates;
	private final URIResolver uriResolverAdapter;
	
	public TransformationContext(final IRenderingContext processCtx, final Collection<URI> schemaFiles, final Collection<URI> xslSources) throws FileNotFoundException {
		this(processCtx,
			createSAXParserFactory(schemaFiles, processCtx.getInputUriResolver()),
			createTransformationTemplates(xslSources, processCtx.getInputUriResolver()),
			processCtx.getInputUriResolver(), processCtx.getOutputUriResolver());
	}
	
	private TransformationContext(final IRenderingContext processCtx, final SAXParserFactory parserFactory, final Templates templates, final IResourceResolver resourceResolver, final IOutputUriResolver outputUriResolver) {
		this.processCtx = processCtx;
		this.resourceResolver = resourceResolver;
		this.outputUriResolver = outputUriResolver;
		this.parserFactory = parserFactory;
		this.templates = templates;
		uriResolverAdapter = new CmsInputURIResolver(resourceResolver);
	}
	
	public TransformationContext createLocalized(final Locale locale) {
		return new TransformationContext(processCtx, parserFactory, templates, resourceResolver.createLocalizedResolver(locale), outputUriResolver.createLocalizedResolver(locale));
	}
	
	public XMLReader createReader() {
		
		try {
			final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			
			reader.setErrorHandler(ERROR_HANDLER);
			
			return reader;
		} catch(Exception e) {
			throw new IllegalStateException("Cannot create SAX parser. " + e, e);
		}
	}
	
	public Transformer createTransformer() {
		try {
			final Transformer transformer = templates.newTransformer();
			
			transformer.setURIResolver(uriResolverAdapter);
			
			return transformer;
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot create transformer. " + e, e);
		}
	}
	
	public void execute(IRenderingJob job) {
		processCtx.execute(job);
	}

	public IResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	public IOutputUriResolver getOutputUriResolver() {
		return outputUriResolver;
	}
}
