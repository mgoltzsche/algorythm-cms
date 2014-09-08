package de.algorythm.cms.common.renderer.impl.xslt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TransformationDelegator implements ContentHandler {

	static private interface IStartElementHandler {
		void handleStartElement(TransformationDelegator h, String uri) throws SAXException;
	}
	
	static private final IStartElementHandler FIRST_ELEMENT_HANDLER = new IStartElementHandler() {
		@Override
		public void handleStartElement(final TransformationDelegator h, final String uri) throws SAXException {
			TransformerHandler transformer = h.renderer.loadTransformer(uri);
			
			transformer.setResult(h.result);
			transformer.startDocument();
			
			for (Entry<String, String> entry : h.prefixMapping.entrySet())
				transformer.startPrefixMapping(entry.getKey(), entry.getValue());
			
			h.transformer = transformer;
			h.startElementHandler = ELEMENT_HANDLER;
		}
	};
	
	static private final IStartElementHandler ELEMENT_HANDLER = new IStartElementHandler() {
		@Override
		public void handleStartElement(final TransformationDelegator h, final String uri) throws SAXException {
		}
	};

	static private final DefaultHandler DEFAULT_HANDLER = new DefaultHandler();
	
	private final XmlContentRenderer renderer;
	private final Result result;
	private final Map<String, String> prefixMapping = new LinkedHashMap<String, String>();
	private ContentHandler transformer = DEFAULT_HANDLER;
	private IStartElementHandler startElementHandler = FIRST_ELEMENT_HANDLER;
	
	public TransformationDelegator(final XmlContentRenderer renderer, final Result result) {
		this.renderer = renderer;
		this.result = result;
	}
	
	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		startElementHandler.handleStartElement(this, uri);
		transformer.startElement(uri, localName, qName, atts);
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		transformer.endElement(uri, localName, qName);
	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		transformer.characters(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		startElementHandler = FIRST_ELEMENT_HANDLER;
	}
	
	@Override
	public void endDocument() throws SAXException {
		transformer.endDocument();
	}

	@Override
	public void startPrefixMapping(final String prefix, final String uri)
			throws SAXException {
		prefixMapping.put(prefix, uri);
		transformer.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(final String prefix) throws SAXException {
		transformer.endPrefixMapping(prefix);
	}

	@Override
	public void ignorableWhitespace(final char[] ch, final int start,
			final int length) throws SAXException {
		transformer.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(final String target, final String data)
			throws SAXException {
		transformer.processingInstruction(target, data);
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		transformer.setDocumentLocator(locator);
	}

	@Override
	public void skippedEntity(final String name) throws SAXException {
		transformer.skippedEntity(name);
	}
}
