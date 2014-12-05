package de.algorythm.cms.common.impl.xml;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class Stax2SaxConverter {

	public void handleStaxEvent(final XMLEvent staxEvent, final ContentHandler saxHandler) throws SAXException {
		final int staxEventType = staxEvent.getEventType();
		
		switch(staxEventType) {
		case START_DOCUMENT:
			saxHandler.setDocumentLocator(new StaxLocatorAdapter(staxEvent.getLocation()));
			saxHandler.startDocument();
			break;
		case END_DOCUMENT:
			saxHandler.endDocument();
			break;
		case START_ELEMENT:
			final StartElement se = staxEvent.asStartElement();
			//saxHandler.startElement(se., localName, qName, atts);
			break;
		case END_ELEMENT:
			final EndElement ee = staxEvent.asEndElement();
			
			break;
		case CHARACTERS:
			final Characters ch = staxEvent.asCharacters();
			final byte[] str = ch.getData().getBytes();
			break;
		default:
			throw new UnsupportedOperationException("Unsupported StAX event type: " + staxEventType);
		}
	}
}
