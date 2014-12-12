package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.rendering.pipeline.impl.Cache.IValueLoader;
import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.XsdResourceResolver;

public class XmlDomLoader implements IXmlLoader, IValueLoader<Path, Document> {

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
	
	private final DocumentBuilderFactory factory;
	private final Cache<Path, Document> domCache;
	
	public XmlDomLoader(final Collection<URI> schemaLocationUris, final IUriResolver uriResolver) throws IOException {
		final Schema schema = createSchema(schemaLocationUris, uriResolver);
		this.domCache = new Cache<Path, Document>();
		factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
	}
	
	@Override
	public Document loadDocument(final Path path) {
		return domCache.get(path, this);
	}
	
	@Override
	public Document populate(final Path systemPath) {
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final InputStream stream = Files.newInputStream(systemPath);
			
			builder.setErrorHandler(ERROR_HANDLER);
			
			return builder.parse(stream);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Schema createSchema(final Collection<URI> schemaLocationUris, final IUriResolver uriResolver) throws IOException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocationUris.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new XsdResourceResolver(uriResolver));
		
		for (URI schemaLocationUri : schemaLocationUris) {
			final Path schemaLocation = uriResolver.resolve(schemaLocationUri);
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
