package de.algorythm.cms.common.impl.xml.contentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SplittingHandler implements ContentHandler {

	static private final DefaultHandler DEFAULT_HANDLER = new DefaultHandler();
	
	private final ContentHandler delegator;
	private final ContentHandler secondaryDelegator;
	private ContentHandler activeSecondaryDelegator = DEFAULT_HANDLER;
	
	public SplittingHandler(final ContentHandler primaryHandler, ContentHandler secondaryHandler) {
		this.delegator = primaryHandler;
		this.secondaryDelegator = secondaryHandler;
	}

	public void enableSecondaryHandler() {
		this.activeSecondaryDelegator = secondaryDelegator;
	}
	
	public void disableSecondaryHandler() throws SAXException {
		activeSecondaryDelegator = DEFAULT_HANDLER;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		delegator.characters(ch, start, length);
		activeSecondaryDelegator.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		delegator.endDocument();
		activeSecondaryDelegator.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		delegator.startElement(uri, localName, qName, atts);
		activeSecondaryDelegator.startElement(uri, localName, qName, atts);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		delegator.endElement(uri, localName, qName);
		activeSecondaryDelegator.endElement(uri, localName, qName);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		delegator.endPrefixMapping(prefix);
		secondaryDelegator.endPrefixMapping(prefix);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		delegator.ignorableWhitespace(ch, start, length);
		activeSecondaryDelegator.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		delegator.processingInstruction(target, data);
		activeSecondaryDelegator.processingInstruction(target, data);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		delegator.setDocumentLocator(locator);
		secondaryDelegator.setDocumentLocator(locator);
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		delegator.skippedEntity(name);
		activeSecondaryDelegator.skippedEntity(name);
	}

	@Override
	public void startDocument() throws SAXException {
		delegator.startDocument();
		secondaryDelegator.startDocument();
		activeSecondaryDelegator = DEFAULT_HANDLER;
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		delegator.startPrefixMapping(prefix, uri);
		secondaryDelegator.startPrefixMapping(prefix, uri);
	}

}
