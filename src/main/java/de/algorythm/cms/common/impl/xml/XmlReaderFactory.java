package de.algorythm.cms.common.impl.xml;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;

public class XmlReaderFactory implements IXmlReaderFactory {

	private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	
	public XmlReaderFactory() {
		try {
			final String staticSchemaDir = "/de/algorythm/cms/common/";
			final Schema schema = createSchema(/*staticSchemaDir + "xhtml1-strict.xsd", */staticSchemaDir + "Site.xsd", staticSchemaDir + "Article.xsd", staticSchemaDir + "Page.xsd");
			//parserFactory.setValidating(true); REQUIRED FOR DTD VALIDATION ONLY
			parserFactory.setNamespaceAware(true);
			//parserFactory.setXIncludeAware(true);
			parserFactory.setSchema(schema); // REQUIRED FOR VALIDATION ONLY
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML content renderer", e);
		}
	}
	
	@Override
	public XMLReader createReader() throws SAXException {
		final SAXParser parser;
		
		try {
			parser = parserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException("Invalid parser configuration", e);
		}
		
		return parser.getXMLReader();
	}
	
	private Schema createSchema(final String... xsdFilePathes) throws SAXException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[xsdFilePathes.length];
		
		for (int i = 0; i < xsdFilePathes.length; i++) {
			final String xsdPath = xsdFilePathes[i];
			final URL xsdUrl = getClass().getResource(xsdPath);
			
			if (xsdUrl == null)
				throw new IllegalStateException("Missing XSD file " + xsdPath);
			
			try {
				sources[i] = new StreamSource(new File(xsdUrl.toURI()));
			} catch(URISyntaxException e) {
				throw new SAXException("Invalid URI: " + xsdUrl);
			}
		}
		
		final Schema schema = schemaFactory.newSchema(sources);
		
		return schema;
	}
}
