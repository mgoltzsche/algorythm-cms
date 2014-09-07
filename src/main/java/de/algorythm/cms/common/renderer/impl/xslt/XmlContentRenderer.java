package de.algorythm.cms.common.renderer.impl.xslt;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Singleton;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;

@Singleton
public class XmlContentRenderer implements IContentRenderer {

	@Override
	public String render(final String content, final String transformation)
			throws RendererException {
		try {
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Schema schema = createSchema("/xsd/algorythm-article.xsd", "/xsd/algorythm-markup.xsd");
			final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			//parserFactory.setValidating(true); REQUIRED FOR DTD VALIDATION ONLY
			parserFactory.setNamespaceAware(true);
			parserFactory.setSchema(schema);
			final SAXParser parser = parserFactory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(XmlParserErrorHandler.INSTANCE);
			final Source xsltSource = new StreamSource(new StringReader(transformation));
			final Source source = new SAXSource(reader, new InputSource(new StringReader(content)));
			final Transformer transformer = transformerFactory.newTransformer(xsltSource);
			final ContentHandler handler = new XmlToStringHandler();
			final Result result = new SAXResult(handler);

			transformer.transform(source, result);
			
			return handler.toString();
		} catch (Exception e) {
			throw new RendererException(e);
		}
	}
	
	private Schema createSchema(final String... xsdFilePathes) throws URISyntaxException, SAXException {
		final String schemaNs = "http://www.w3.org/2001/XMLSchema";
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaNs);
		final Source[] sources = new Source[xsdFilePathes.length];
		
		for (int i = 0; i < xsdFilePathes.length; i++) {
			final String xsdPath = xsdFilePathes[i];
			final URL xsdUrl = getClass().getResource(xsdPath);
			
			if (xsdUrl == null)
				throw new IllegalStateException("Missing XSD file " + xsdPath);
			
			sources[i] = new StreamSource(new File(xsdUrl.toURI()));
		}
		
		final Schema schema = schemaFactory.newSchema(sources);
		
		return schema;
	}
}
