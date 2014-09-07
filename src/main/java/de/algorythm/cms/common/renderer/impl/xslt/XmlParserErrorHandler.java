package de.algorythm.cms.common.renderer.impl.xslt;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlParserErrorHandler implements ErrorHandler {

	static public XmlParserErrorHandler INSTANCE = new XmlParserErrorHandler();
	
	private XmlParserErrorHandler() {}
	
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
