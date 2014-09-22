package de.algorythm.cms.common.impl.xml.contentHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.impl.xml.Constants.Attribute;
import de.algorythm.cms.common.impl.xml.Constants.Namespace;
import de.algorythm.cms.common.impl.xml.Constants.Tag;
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.resources.IResourceUriResolver;

public class IncludingHandler implements ContentHandler, ErrorHandler {

	private final IXmlReaderFactory readerFactory;
	private ContentHandler delegator;
	private final Stack<Locator> locators = new Stack<Locator>();
	private final IResourceUriResolver contentUriResolver;
	
	public IncludingHandler(final IXmlReaderFactory readerFactory, final IResourceUriResolver contentUriResolver) {
		this.readerFactory = readerFactory;
		this.contentUriResolver = contentUriResolver;
	}
	
	public void setDelegator(final ContentHandler handler) {
		locators.clear();
		this.delegator = handler;
	}
	
	private boolean isInclude(final String uri, final String localName) {
		return Namespace.CMS.equals(uri) && Tag.INCLUDE.equals(localName);
	}
	
	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		if (isInclude(uri, localName)) {
			final String ref = atts.getValue(Attribute.HREF);
			
			if (ref == null)
				throw new SAXException("Attribute '" + Attribute.HREF + "' of " + Namespace.CMS + ':' + Tag.INCLUDE + " must be set");
			
			final Locator locator = locators.peek();
			final String systemId = locator.getSystemId();
			final URI refSystemUri = contentUriResolver.toSystemUri(toUri(systemId), toUri(ref));
			final XMLReader reader = readerFactory.createReader();
			
			reader.setContentHandler(this);
			reader.setErrorHandler(this);
			
			try {
				reader.parse(refSystemUri.toString());
			} catch (IOException e) {
				throw new SAXException("Cannot include " + refSystemUri + " into " + systemId, e);
			}
		} else
			delegator.startElement(uri, localName, qName, atts);
	}
	
	private URI toUri(final String uri) throws SAXException {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new SAXException("Invalid URI: " + uri, e);
		}
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		if (!isInclude(uri, localName))
			delegator.endElement(uri, localName, qName);
	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		delegator.characters(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		if (locators.size() == 1)
			delegator.startDocument();
	}
	
	@Override
	public void endDocument() throws SAXException {
		locators.pop();
		
		if (locators.isEmpty())
			delegator.endDocument();
	}

	@Override
	public void startPrefixMapping(final String prefix, final String uri)
			throws SAXException {
		delegator.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(final String prefix) throws SAXException {
		delegator.endPrefixMapping(prefix);
	}

	@Override
	public void ignorableWhitespace(final char[] ch, final int start,
			final int length) throws SAXException {
		delegator.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(final String target, final String data)
			throws SAXException {
		delegator.processingInstruction(target, data);
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		locators.push(locator);
		
		if (locators.size() == 1) {
			delegator.setDocumentLocator(locator);
		}
	}

	@Override
	public void skippedEntity(final String name) throws SAXException {
		delegator.skippedEntity(name);
	}
	
	@Override
	public void error(final SAXParseException e) throws SAXException {
		throw e;
	}

	@Override
	public void fatalError(final SAXParseException e) throws SAXException {
		throw e;
	}

	@Override
	public void warning(final SAXParseException e) throws SAXException {
		throw e;
	}
}
