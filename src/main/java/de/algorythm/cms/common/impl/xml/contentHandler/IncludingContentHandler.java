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

import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;

public class IncludingContentHandler implements ContentHandler, ErrorHandler {

	static private final String NAMESPACE = "http://cms.algorythm.de/common/Types";
	static private final String INCLUDE = "include";
	static private final String HREF = "href";
	
	private final IXmlReaderFactory readerFactory;
	private final ContentHandler delegator;
	private final Stack<Locator> locators = new Stack<Locator>();
	
	public IncludingContentHandler(final IXmlReaderFactory readerFactory, final ContentHandler delegator) {
		this.readerFactory = readerFactory;
		this.delegator = delegator;
	}
	
	private boolean isInclude(final String uri, final String localName) {
		return NAMESPACE.equals(uri) && INCLUDE.equals(localName);
	}
	
	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		if (isInclude(uri, localName)) {
			final String ref = atts.getValue(HREF);
			
			if (ref == null)
				throw new SAXException("Attribute '" + HREF + "' of " + NAMESPACE + ':' + INCLUDE + " must be set");
			
			final String refLocation = locators.peek().getSystemId();
			final URI locationUri;
			
			try {
				locationUri = new URI(refLocation);
			} catch (URISyntaxException e) {
				throw new SAXException("Unsupported document location URI: " + refLocation);
			}
			
			final String absoluteRef = locationUri.resolve(ref).toString();
			final XMLReader reader = readerFactory.createReader();
			
			reader.setContentHandler(this);
			
			try {
				reader.parse(absoluteRef);
			} catch (IOException e) {
				throw new SAXException("Cannot read " + absoluteRef);
			}
		} else
			delegator.startElement(uri, localName, qName, atts);
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
		
		if (locators.size() == 1)
			delegator.setDocumentLocator(locator);
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
