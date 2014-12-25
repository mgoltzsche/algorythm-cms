package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.xml.sax.SAXException;

public interface IXmlLoader {

	Source getSource(URI publicUri, Locale locale) throws SAXException, ParserConfigurationException, IOException;
}
