package de.algorythm.cms.common.impl.xml.contentHandler;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class Xml2StringHandler implements ContentHandler {

	private final StringBuilder content = new StringBuilder();
	private final LinkedList<String> prefixMappings = new LinkedList<String>();

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		content.append('<').append(qName);
		
		for (int i = 0; i < atts.getLength(); i++) {
			content.append(' ').append(atts.getQName(i))
				.append("=\"").append(StringEscapeUtils.escapeXml(atts.getValue(i))).append('"');
		}
		
		final Iterator<String> prefixIter = prefixMappings.iterator();
		
		while (prefixIter.hasNext()) {
			content.append(prefixIter.next());
			prefixIter.remove();
		}
		
		content.append('>');
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		content.append("</").append(qName).append('>');
	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		content.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
	}
	
	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startPrefixMapping(String prefix, final String uri)
			throws SAXException {
		if (!prefix.isEmpty())
			prefix = ':' + prefix;
		
		prefixMappings.add(" xmlns" + prefix + "=\"" + uri + "\"");
	}

	@Override
	public void endPrefixMapping(final String prefix) throws SAXException {

	}

	@Override
	public void ignorableWhitespace(final char[] ch, final int start,
			final int length) throws SAXException {
	}

	@Override
	public void processingInstruction(final String target, final String data)
			throws SAXException {
		throw new SAXException("Unsupported instruction: " + target + " - " + data);
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
	}

	@Override
	public void skippedEntity(final String name) throws SAXException {
		throw new SAXException("Skipped entity " + name);
	}
	
	@Override
	public String toString() {
		return content.toString();
	}
}
