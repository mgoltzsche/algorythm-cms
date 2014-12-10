package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.XsdResourceResolver;

public class TransformationContextInitializationUtil {
	
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
	
	static public Templates createTransformationTemplates(final Collection<Path> xslSources, final IUriResolver uriResolver) {
		final Source xslSource = createMergedXslSource(xslSources);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		transformerFactory.setURIResolver(new CmsInputURIResolver(uriResolver));
		
		try {
			return transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot load XSL templates. " + e, e);
		}
	}
	
	static public Source createMergedXslSource(final Collection<Path> xslSources) {
		if (xslSources.size() == 1)
			return new StreamSource(xslSources.iterator().next().toString());
		
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (Path source : xslSources) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(source.toString()))
				.append("\" />");
		}
		
		final String mergedXsl = xslt.append("</xsl:stylesheet>").toString();
		final Reader mergedTplReader = new StringReader(mergedXsl);
		
		return new StreamSource(mergedTplReader);
	}
	
	static public SAXParserFactory createSAXParserFactory(final Collection<Path> schemaLocations, final IUriResolver uriResolver) {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final Schema schema = createSchema(schemaLocations, uriResolver);
		
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema); // REQUIRED FOR VALIDATION ONLY
		
		return parserFactory;
	}
	
	static public Schema createSchema(final Collection<Path> schemaLocations, final IUriResolver uriResolver) {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocations.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new XsdResourceResolver(uriResolver));
		
		for (Path schemaLocation : schemaLocations)
			sources[i++] = new StreamSource(uriResolver.resolve(schemaLocation).toString());
		
		try {
			return schemaFactory.newSchema(sources);
			//schema = schemaFactory.newSchema(new StreamSource(new File("/home/max/development/java/algorythm-cms/target/classes/de/algorythm/cms/common/types/CMS.xsd")));
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML schema. " + e, e);
		}
	}
}
