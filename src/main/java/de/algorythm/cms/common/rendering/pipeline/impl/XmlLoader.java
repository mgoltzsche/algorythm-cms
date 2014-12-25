package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsSchemaResolver;

public class XmlLoader implements IXmlLoader {

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

	private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private final ISourceUriResolver uriResolver;
	
	public XmlLoader(final Collection<URI> schemaLocationUris, final ISourceUriResolver uriResolver) throws Exception {
		this.uriResolver = uriResolver;
		//final Schema schema = createSchema(schemaLocationUris, sourceUriResolver);
		
		//factory.setSchema(schema);
		parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(false);
		parserFactory.setXIncludeAware(false);
	}
	
	@Override
	public Source getSource(final URI publicUri, final Locale locale) throws SAXException, ParserConfigurationException, IOException {
		final Path path = uriResolver.resolve(publicUri, locale);
		final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		final InputStream stream = Files.newInputStream(path);
		final InputSource inputSource = new InputSource(stream);
		final Source source = new SAXSource(reader, inputSource);
		
		reader.setErrorHandler(ERROR_HANDLER);
		source.setSystemId(publicUri.toString());
		
		return source;
	}

	private Schema createSchema(final Collection<URI> schemaLocationUris, final ISourceUriResolver sourceUriResolver) throws Exception {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocationUris.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new CmsSchemaResolver(sourceUriResolver));
		
		for (URI schemaLocationUri : schemaLocationUris) {
			final Path schemaLocation = sourceUriResolver.resolve(schemaLocationUri, Locale.ROOT);
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
}
