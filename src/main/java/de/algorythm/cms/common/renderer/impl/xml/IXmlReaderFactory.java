package de.algorythm.cms.common.renderer.impl.xml;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public interface IXmlReaderFactory {

	XMLReader createReader() throws SAXException;
}
