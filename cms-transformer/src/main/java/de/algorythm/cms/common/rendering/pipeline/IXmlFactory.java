package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public interface IXmlFactory extends IXmlSourceResolver {

	void parse(URI publicUri, ContentHandler handler, IRenderingContext ctx) throws IOException, SAXException, ParserConfigurationException, ResourceNotFoundException;
	Templates compileTemplates(Collection<URI> xslSourceUris, IRenderingContext ctx) throws TransformerConfigurationException;
	Templates compileTemplates(URI xslSourceUri, IRenderingContext ctx) throws TransformerConfigurationException, ResourceNotFoundException;
	XMLReader createXMLReader() throws SAXException;
	TransformerHandler createTransformerHandler(Templates templates, IRenderingContext ctx, String outputPath, IOutputTargetFactory outFactory) throws TransformerConfigurationException, IOException;
	XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException;
	Marshaller createMarshaller() throws JAXBException;
	Unmarshaller createUnmarshaller() throws JAXBException;
	//ContentHandler createXMLWriter(OutputStream out) throws TransformerConfigurationException;
	//XMLFilter createXMLFilter(Templates templates, XMLReader parent) throws TransformerConfigurationException;
}
