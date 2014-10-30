package de.algorythm.cms.common.impl.xml.contentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.algorythm.cms.common.impl.xml.Constants.Namespace;
import de.algorythm.cms.common.impl.xml.Constants.Tag;


public class SplittingHandlerSwitchingHandler implements ContentHandler {

	static private interface IElementSwitchHandler {
		
		void startElement(SplittingHandlerSwitchingHandler handler, String uri, String localName);
		void endElement(SplittingHandlerSwitchingHandler handler, String uri, String localName) throws SAXException;
	}
	
	static private final IElementSwitchHandler NONE = new IElementSwitchHandler() {
		
		@Override
		public void startElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
		}
		
		@Override
		public void endElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
		}
	};
	
	static private final IElementSwitchHandler INITIAL = new IElementSwitchHandler() {
		
		@Override
		public void startElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
			if (Namespace.CMS.equals(uri) &&
					(Tag.PAGE.equals(localName) || Tag.SITE.equals(localName)))
				// If is page element
				handler.switchStrategy = PAGE_ELEMENT;
		}
		
		@Override
		public void endElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
		}
	};
	
	static private final IElementSwitchHandler PAGE_ELEMENT = new IElementSwitchHandler() {
		
		@Override
		public void startElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
			handler.switchStrategy = PAGE_CONTENT;
			handler.contentDepth = 0;
			handler.splittingHandler.enableSecondaryHandler();
		}
		
		@Override
		public void endElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
			handler.switchStrategy = NONE;
		}
	};
	
	static private final IElementSwitchHandler PAGE_CONTENT = new IElementSwitchHandler() {
		
		@Override
		public void startElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) {
			handler.contentDepth++;
		}
		
		@Override
		public void endElement(SplittingHandlerSwitchingHandler handler,
				String uri, String localName) throws SAXException {
			if (--handler.contentDepth == 0) {
				handler.switchStrategy = NONE;
				handler.splittingHandler.disableSecondaryHandler();
			}
		}
	};

	private ContentHandler delegator;
	private final SplittingHandler splittingHandler;
	private IElementSwitchHandler switchStrategy;
	private int contentDepth;

	public SplittingHandlerSwitchingHandler(final SplittingHandler splittingHandler) {
		this.splittingHandler = splittingHandler;
	}

	public void setDelegator(final ContentHandler delegator) {
		this.delegator = delegator;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		switchStrategy.startElement(this, uri, localName);
		delegator.startElement(uri, localName, qName, atts);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		delegator.endElement(uri, localName, qName);
		switchStrategy.endElement(this, uri, localName);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		delegator.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		delegator.endDocument();
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		delegator.endPrefixMapping(prefix);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		delegator.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		delegator.processingInstruction(target, data);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		delegator.setDocumentLocator(locator);
		switchStrategy = INITIAL;
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		delegator.skippedEntity(name);
	}

	@Override
	public void startDocument() throws SAXException {
		delegator.startDocument();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		delegator.startPrefixMapping(prefix, uri);
	}
}
