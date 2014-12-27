package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public interface IXmlContext {

	void parse(URI publicUri, ContentHandler handler) throws IOException, SAXException, ParserConfigurationException;
	Source getSource(URI publicUri) throws IOException, SAXException, ParserConfigurationException;
	Templates compileTemplates(Collection<URI> xslSourceUris) throws TransformerConfigurationException;
	Templates compileTemplates(URI xslSourceUri) throws TransformerConfigurationException;
	XMLReader createXMLReader() throws SAXException;
	ContentHandler createXMLWriter(URI publicUri) throws IOException, TransformerConfigurationException;
	TransformerHandler createTransformerHandler(Templates templates, URI outputUri) throws TransformerConfigurationException, IOException;
	XMLFilter createXMLFilter(Templates templates, XMLReader parent) throws TransformerConfigurationException;
}
