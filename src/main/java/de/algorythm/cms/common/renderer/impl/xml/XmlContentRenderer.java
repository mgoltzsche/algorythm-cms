package de.algorythm.cms.common.renderer.impl.xml;

import static de.algorythm.cms.common.ParameterNameConstants.Render.OUTPUT_DIRECTORY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGES_XML;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RELATIVE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.REPOSITORY_DIRECTORY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.vaadin.sass.internal.ScssStylesheet;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.impl.xml.contentHandler.IncludingHandler;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;
import de.algorythm.cms.common.resources.IResourceUriResolver;
import de.algorythm.cms.common.resources.impl.ContentUriResolver;

@Singleton
public class XmlContentRenderer implements IContentRenderer {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";

	private final File repositoryDirectory;
	private final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
	private final IXmlReaderFactory readerFactory;
	private final IResourceUriResolver contentUriResolver;

	@Inject
	public XmlContentRenderer(final Configuration cfg,
			final IXmlReaderFactory readerFactory) {
		this.repositoryDirectory = cfg.repository;
		this.readerFactory = readerFactory;
		this.contentUriResolver = new ContentUriResolver(cfg);
	}

	@Override
	public void render(final ISite site, final File outputDirectory)
			throws RendererException {
		final String timestamp = String.valueOf(new Date().getTime());
		System.out.println(site.getName() + "  " + timestamp);
		File resOutputDirectory = new File(outputDirectory, "r");
		resOutputDirectory = new File(resOutputDirectory, timestamp);

		compileResources(site, resOutputDirectory);
		render(site, site.getStartPage(), outputDirectory, timestamp);
	}

	private void compileResources(final ISite site, final File outputDirectory)
			throws RendererException {
		final File siteDirectory = new File(repositoryDirectory, site.getName());
		final File cssDirectory = new File(siteDirectory, "css");
		final File cssOutputDirectory = new File(outputDirectory, "css");

		if (!siteDirectory.exists())
			throw new RendererException("Site directory " + siteDirectory + " does not exist");

		if (cssDirectory.exists()) {
			if (!cssDirectory.isDirectory())
				throw new RendererException(cssDirectory + " is not a directory");

			try {
				compileSCSS(cssDirectory, cssOutputDirectory);
			} catch (Exception e) {
				throw new RendererException("Cannot compile SCSS", e);
			}
		}
		
		try {
			copyResources(site, outputDirectory);
		} catch(IOException e) {
			throw new RendererException("Cannot copy resources", e);
		}
	}

	private void compileSCSS(final File inputDirectory,
			final File outputDirectory) throws Exception {
		final File mainScssFile = new File(inputDirectory, "main.scss");

		if (mainScssFile.exists()) {
			final File outputFile = new File(outputDirectory, "main.css");
			final String output = compileSCSS(mainScssFile);

			outputDirectory.mkdirs();
			FileUtils.writeStringToFile(outputFile, output);
		}

		for (File file : inputDirectory.listFiles())
			if (file.isDirectory())
				compileSCSS(file, new File(outputDirectory, file.getName()));
	}

	private String compileSCSS(final File scssFile) throws Exception {
		final ScssStylesheet scss = ScssStylesheet.get(scssFile
				.getAbsolutePath());

		if (scss == null)
			throw new IOException("Cannot find SCSS file " + scssFile);

		scss.compile();

		return scss.printState();
	}
	
	private void copyResources(final ISite site, final File outputDirectory) throws IOException {
		final File siteDirectory = new File(repositoryDirectory, site.getName());
		
		for (String resDirName : new String[] { "scripts", "img" }) {
			final File resDirectory = new File(siteDirectory, resDirName);
			final File outDirectory = new File(outputDirectory, resDirName);

			if (resDirectory.exists())
				FileUtils.copyDirectory(resDirectory, outDirectory);
		}
	}

	private void render(final ISite site, final IPage page,
			final File outputDirectory,
			final String currentResourceDirectoryName) throws RendererException {
		outputDirectory.mkdirs();

		final String siteName = site.getName();
		final String pagePath = page.getPath();
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final File siteDirectory = new File(repositoryDirectory, siteName);
		final File pagesFile = new File(siteDirectory, "pages.xml");
		final File pageFile = new File(siteDirectory.getAbsolutePath()
				+ "/pages" + pagePath.replace('/', File.separatorChar)
				+ File.separator + "page.xml");

		// Render page
		render(pageFile, pagesFile, outputDirectory, relativeBaseUrl, currentResourceDirectoryName);

		// Render sub pages
		for (IPage child : page.getPages())
			render(site, child, new File(outputDirectory, child.getName()), currentResourceDirectoryName);
	}

	private void render(final File contentFile, final File pagesFile,
			final File outputDirectory, final String relativeBaseUrl,
			final String currentResourceDirectoryName) throws RendererException {
		try {
			// final TransformerDelegator transformerDelegator = new
			// TransformerDelegator(this, new StreamResult(writer));
			final XMLReader reader = readerFactory.createReader();
			// final Xml2StringHandler fullOutput = new Xml2StringHandler();
			// final Xml2StringHandler partialOutput = new Xml2StringHandler();
			// final SplittingHandler splittingHandler = new
			// SplittingHandler(fullOutput, partialOutput);
			// final SplittingHandlerSwitchingHandler switchHandler = new
			// SplittingHandlerSwitchingHandler(splittingHandler);
			final IncludingHandler handler = new IncludingHandler(readerFactory, contentUriResolver);
			final TransformerHandler transformerHandler = createTransformer();
			final Transformer transformer = transformerHandler.getTransformer();
			// final FileWriter writer = new FileWriter(new
			// File(outputDirectory, "index.html"));
			final StreamResult result = new StreamResult(new File(outputDirectory, "index.html"));
			transformer.setParameter(REPOSITORY_DIRECTORY, repositoryDirectory.toString());
			transformer.setParameter(OUTPUT_DIRECTORY, outputDirectory.toString());
			transformer.setParameter(PAGES_XML, pagesFile.toString());
			transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
			transformer.setParameter(RESOURCE_DIRECTORY, relativeBaseUrl + "/r/" + currentResourceDirectoryName);
			// transformerHandler.setSystemId(contentFile.toString());
			// result.setSystemId(contentFile);
			transformerHandler.setResult(result);
			// transformer.setResult(new SAXResult(splittingHandler));
			handler.setDelegator(transformerHandler);
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);
			reader.parse(contentFile.getAbsolutePath());
			// System.out.println("#######################");
			// System.out.println(partialOutput);
		} catch (TransformerConfigurationException e) {
			throw new RendererException("Invalid transformer configuration", e);
		} catch (Exception e) {
			throw new RendererException(e.getMessage(), e);
		}
	}

	private TransformerHandler createTransformer()
			throws TransformerConfigurationException {
		final String xslUri = "/de/algorythm/cms/common/tpl/include-view-html.xsl";
		final URL url = getClass().getResource(xslUri);

		if (url == null)
			throw new IllegalStateException("Missing XSL template: " + xslUri);

		final Templates tpls = transformerFactory.newTemplates(new StreamSource(url.toString()));
		final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(tpls);
		final Transformer transformer = transformerHandler.getTransformer();
		transformer.setErrorListener(XslErrorListener.INSTANCE);

		return transformerHandler;
	}

	public TransformerHandler loadTransformer(final String uri)
			throws SAXException {
		final File xslFile = deriveXslFile(uri, "html");

		try {
			final Templates tpls = transformerFactory.newTemplates(new StreamSource(xslFile));
			final TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(tpls);
			final Transformer transformer = transformerHandler.getTransformer();
			transformer.setErrorListener(XslErrorListener.INSTANCE);

			return transformerHandler;
		} catch (Exception e) {
			throw new SAXException("Cannot load transformer for " + uri, e);
		}
	}

	private File deriveXslFile(final String typeUri, final String outputFormat)
			throws SAXException {
		final URI uri;

		try {
			uri = new URI(typeUri);
		} catch (URISyntaxException e) {
			throw new SAXException("Invalid type URI: " + typeUri, e);
		}

		final String[] hostSegments = uri.getHost().split("\\.");
		final String[] pathSegments = uri.getPath().split("/");
		final StringBuilder sb = new StringBuilder();

		for (int i = hostSegments.length - 1; i >= 0; i--)
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
			throw new SAXException("Missing XSL template: "
					+ xslFile.getAbsolutePath());

		if (!xslFile.isFile())
			throw new SAXException("Cannot read XSL template "
					+ xslFile.getAbsolutePath() + " since it is not a file");

		if (!xslFile.canRead())
			throw new SAXException("Cannot read XSL template "
					+ xslFile.getAbsolutePath()
					+ " due to file system restrictions");

		return xslFile;
	}

	private String relativeBaseUrl(final String path) {
		final int depth = pathDepth(path);

		if (depth == 0)
			return DOT;

		final StringBuilder sb = new StringBuilder((depth - 1) * 3 + 2);

		for (int i = 1; i < depth; i++)
			sb.append(BACK_SLASH);

		sb.append(BACK);

		return sb.toString();
	}

	private int pathDepth(final String path) {
		final int pathLength = path.length();
		int depth = 0;

		for (int i = 2; i < pathLength; i++)
			if (path.charAt(i) == '/')
				depth++;

		return depth;
	}
}
