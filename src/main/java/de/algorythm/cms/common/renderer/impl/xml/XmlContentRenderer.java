package de.algorythm.cms.common.renderer.impl.xml;

import java.io.File;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.impl.xml.contentHandler.ContentSplittingHandler;
import de.algorythm.cms.common.impl.xml.contentHandler.IncludingHandler;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;
import de.algorythm.cms.common.resources.IResourceUriResolver;
import de.algorythm.cms.common.resources.impl.ContentUriResolver;

@Singleton
public class XmlContentRenderer implements IContentRenderer {

	private final File repositoryDirectory;
	private final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
	private final IXmlReaderFactory readerFactory;
	private final IResourceUriResolver contentUriResolver;
	
	@Inject
	public XmlContentRenderer(final Configuration cfg, final IXmlReaderFactory readerFactory) {
		this.repositoryDirectory = cfg.repository;
		this.readerFactory = readerFactory;
		this.contentUriResolver = new ContentUriResolver(cfg);
	}
	
	@Override
	public void render(final ISite site, final IPage page, final Writer writer) throws RendererException {
		final String siteName = site.getName();
		final String pagePath = page.getPath();
		
		render(new File(repositoryDirectory.getAbsolutePath() + File.separator + siteName + File.separator + "pages" + pagePath.replaceAll("/", File.separator) + File.separator + "page.xml"), writer);
	}
	
	@Override
	public void render(final File contentFile, final Writer writer) throws RendererException {
		try {
			//final TransformerDelegator transformerDelegator = new TransformerDelegator(this, new StreamResult(writer));
			final XMLReader reader = readerFactory.createReader();
			final IncludingHandler handler = new IncludingHandler(readerFactory, contentUriResolver);
			final TransformerHandler transformer = createTransformer();
			
			transformer.setResult(new StreamResult(writer));
			//transformer.setResult(new SAXResult(new ContentSplittingHandler(delegator1, delegator2)));
			handler.setDelegator(transformer);
			reader.setErrorHandler(handler);
			reader.setContentHandler(handler);
			reader.parse(contentFile.getAbsolutePath());
		} catch (TransformerConfigurationException e) {
			throw new RendererException("Invalid transformer configuration", e);
		} catch (Exception e) {
			throw new RendererException(e.getMessage(), e);
		}
	}
	
	private TransformerHandler createTransformer() throws TransformerConfigurationException {
		final String xslUri = "/de/algorythm/cms/common/tpl/include-view-html.xsl";
		final URL url = getClass().getResource(xslUri);
		
		if (url == null)
			throw new IllegalStateException("Missing XSL template: " + xslUri);
		
		final Templates tpls = transformerFactory.newTemplates(new StreamSource(url.toString()));
		final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(tpls);
		final Transformer transformer = transformerHandler.getTransformer();
		transformer.setParameter("repositoryDirectory", repositoryDirectory);
		transformer.setErrorListener(XslErrorListener.INSTANCE);
		
		return transformerHandler;
	}
	
	public TransformerHandler loadTransformer(final String uri) throws SAXException {
		final File xslFile = deriveXslFile(uri, "html");
		
		try {
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
		
		for (int i = 1; i < pathSegments.length - 1; i++)
			sb.append(File.separator).append(pathSegments[i]);
		
		sb.append(File.separator).append("tpl").append(File.separator)
			.append(outputFormat).append(File.separator)
			.append(pathSegments[pathSegments.length - 1]).append(".xsl");
		
		final String fileName = sb.toString();
		final URL fileUrl = getClass().getResource(fileName);
		
		if (fileUrl == null)
			throw new SAXException("Missing XSL template: " + fileName);
		
		final File xslFile;
		
		try {
			xslFile = new File(fileUrl.toURI());
		} catch (URISyntaxException e) {
			throw new SAXException("Invalid XSL template URI: " + fileUrl, e);
		}
		
		if (!xslFile.exists())
			throw new SAXException("Missing XSL template: " + xslFile.getAbsolutePath());
		
		if (!xslFile.isFile())
			throw new SAXException("Cannot read XSL template " + xslFile.getAbsolutePath() + " since it is not a file");
		
		if (!xslFile.canRead())
			throw new SAXException("Cannot read XSL template " + xslFile.getAbsolutePath() + " due to file system restrictions");
		
		return xslFile;
	}
}
