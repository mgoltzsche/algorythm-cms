package de.algorythm.cms.common.renderer.impl.xslt;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Singleton;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;

@Singleton
public class XmlContentRenderer implements IContentRenderer {

	@Override
	public String render(final File contentFile) throws RendererException {
		try {
			final String staticSchemaDir = "/de/algorythm/cms/common/";
			final Schema schema = createSchema(staticSchemaDir + "Article.xsd", staticSchemaDir + "Markup.xsd");
			final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			//parserFactory.setValidating(true); REQUIRED FOR DTD VALIDATION ONLY
			parserFactory.setNamespaceAware(true);
			parserFactory.setSchema(schema);
			final SAXParser parser = parserFactory.newSAXParser();
			final XMLReader reader = parser.getXMLReader();
//			final Source source = new StreamSource(contentFile);
			final StringWriter writer = new StringWriter();
			final TransformationDelegator transformerDelegator = new TransformationDelegator(this, new StreamResult(writer));
			
			reader.setErrorHandler(XmlParserErrorHandler.INSTANCE);
			reader.setContentHandler(transformerDelegator);
			reader.parse(contentFile.getAbsolutePath());
			
			return writer.toString();
		} catch (Exception e) {
			throw new RendererException(e);
		}
	}
	
	public TransformerHandler loadTransformer(final String uri) throws SAXException {
		final File xslFile = deriveXslFile(uri, "html");
		
		try {
			final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			final Templates tpls = transformerFactory.newTemplates(new StreamSource(xslFile));
			final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(tpls);
			final Transformer transformer = transformerHandler.getTransformer();
			transformer.setErrorListener(XslErrorListener.INSTANCE);
			
			return transformerHandler;
		} catch(Exception e) {
			throw new SAXException("Cannot load transformer for " + uri, e);
		}
	}
	
	private File deriveXslFile(final String typeUri, final String outputFormat) throws SAXException {
		final URI uri;
		
		try {
			uri = new URI(typeUri);
		} catch (URISyntaxException e) {
			throw new SAXException("Invalid type URI: " + typeUri, e);
		}
		
		final String[] hostSegments = uri.getHost().split("\\.");
		final String[] pathSegments = uri.getPath().split("/");
		final StringBuilder sb = new StringBuilder();
		
		for (int i = hostSegments.length - 1; i >=0; i--)
			sb.append(File.separator).append(hostSegments[i]);
		
		for (int i = 0; i < pathSegments.length - 1; i++)
			sb.append(File.separator).append(pathSegments[i]);
		
		sb.append(File.separator).append("transform").append(File.separator)
			.append(outputFormat).append(File.separator)
			.append(pathSegments[pathSegments.length - 1]).append(".xsl");
		
		final String fileName = sb.toString();
		final URL fileUrl = getClass().getResource(fileName);
		
		if (fileUrl == null)
			throw new SAXException("Missing XLS template: " + fileName);
		
		final File xslFile;
		
		try {
			xslFile = new File(fileUrl.toURI());
		} catch (URISyntaxException e) {
			throw new SAXException("Invalid XLS template URI: " + fileUrl, e);
		}
		
		if (!xslFile.exists())
			throw new SAXException("Missing XSL template: " + xslFile.getAbsolutePath());
		
		if (!xslFile.isFile())
			throw new SAXException("Cannot read XSL template " + xslFile.getAbsolutePath() + " since it is not a file");
		
		if (!xslFile.canRead())
			throw new SAXException("Cannot read XSL template " + xslFile.getAbsolutePath() + " due to file system restrictions");
		
		return xslFile;
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
