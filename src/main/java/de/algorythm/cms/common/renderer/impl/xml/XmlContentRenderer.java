package de.algorythm.cms.common.renderer.impl.xml;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_TITLE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.*;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_DIRECTORY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_NAME;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.vaadin.sass.internal.ScssStylesheet;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RenderingException;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.resources.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.impl.CmsOutputURIResolver;

@Singleton
public class XmlContentRenderer implements IContentRenderer {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";
	
	private final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
	private final IXmlReaderFactory readerFactory;

	@Inject
	public XmlContentRenderer(final Configuration cfg,
			final IXmlReaderFactory readerFactory) {
		transformerFactory.setErrorListener(XslErrorListener.INSTANCE);
		this.readerFactory = readerFactory;
	}

	@Override
	public void render(final IBundle bundle, final IPage startPage, final IResourceResolver uriResolver, final File outputDirectory)
			throws RenderingException {
		final String timestamp = String.valueOf(new Date().getTime());
		System.out.println(bundle.getName() + "  " + timestamp);
		File resOutputDirectory = new File(outputDirectory, "r");
		resOutputDirectory = new File(resOutputDirectory, timestamp);

		compileResources(bundle, resOutputDirectory);
		render(bundle, startPage, uriResolver, outputDirectory, timestamp);
	}

	private void compileResources(final IBundle bundle, final File outputDirectory)
			throws RenderingException {
		final File siteDirectory = new File(bundle.getLocation());
		final File cssDirectory = new File(siteDirectory, "css");
		final File cssOutputDirectory = new File(outputDirectory, "css");

		if (!siteDirectory.exists())
			throw new RenderingException("Site directory " + siteDirectory + " does not exist");

		if (cssDirectory.exists()) {
			if (!cssDirectory.isDirectory())
				throw new RenderingException(cssDirectory + " is not a directory");

			try {
				compileSCSS(cssDirectory, cssOutputDirectory);
			} catch (Exception e) {
				throw new RenderingException("Cannot compile SCSS", e);
			}
		}
		
		try {
			copyResources(bundle, outputDirectory);
		} catch(IOException e) {
			throw new RenderingException("Cannot copy resources", e);
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
		final ScssStylesheet scss = ScssStylesheet.get(scssFile.getAbsolutePath());
		/*scss.addResolver(new ScssStylesheetResolver() {
			
			@Override
			public org.w3c.css.sac.InputSource resolve(ScssStylesheet parentStylesheet,
					String identifier) {
				// TODO Auto-generated method stub
				return null;
			}
		});*/
		if (scss == null)
			throw new IOException("Cannot find SCSS file " + scssFile);

		scss.compile();

		return scss.printState();
	}
	
	private void copyResources(final IBundle bundle, final File outputDirectory) throws IOException {
		final File siteDirectory = new File(bundle.getLocation());
		
		for (String resDirName : new String[] { "scripts", "img" }) {
			final File resDirectory = new File(siteDirectory, resDirName);
			final File outDirectory = new File(outputDirectory, resDirName);

			if (resDirectory.exists())
				FileUtils.copyDirectory(resDirectory, outDirectory);
		}
	}

	public void renderPage(final IBundle bundle, final IPage page, final IResourceResolver uriResolver,
			final File outputDirectory,
			final String currentResourceDirectoryName) throws RenderingException {
		outputDirectory.mkdirs();
		
		final File siteDirectory = new File(bundle.getLocation());
		final File pageFile = new File(siteDirectory + File.separator
				+ "pages" + page.getPath().replace('/', File.separatorChar)
				+ File.separator + "page.xml");
		
		// Render page
		render(pageFile, bundle, page, uriResolver, outputDirectory, currentResourceDirectoryName);
	}
	
	private void render(final IBundle bundle, final IPage page, final IResourceResolver uriResolver,
			final File outputDirectory,
			final String currentResourceDirectoryName) throws RenderingException {
		outputDirectory.mkdirs();
		
		final File directory = new File(bundle.getLocation());
		final File pageFile = new File(directory + File.separator
				+ "pages" + page.getPath().replace('/', File.separatorChar)
				+ File.separator + "page.xml");
		
		// Render page
		render(pageFile, bundle, page, uriResolver, outputDirectory, currentResourceDirectoryName);
		
		// Render sub pages
		for (IPage child : page.getPages())
			render(bundle, child, uriResolver, outputDirectory, currentResourceDirectoryName);
	}

	private void render(final File contentFile, final IBundle bundle, final IPage page, final IResourceResolver uriResolver,
			final File outputDirectory,	final String currentResourceDirectoryName) throws RenderingException {
		final String name = bundle.getName();
		final String pagePath = page.getPath();
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final URI outputDirectoryUri = outputDirectory.toURI();
		final String outputDirUriStr = outputDirectoryUri.toString();
		final URI outputFileUri = URI.create(outputDirUriStr.substring(0, outputDirUriStr.length() - 1) + page.getPath() + "/index.html");
		final File outputFile = new File(outputFileUri);
		final URIResolver templateUriResolver = new CmsInputURIResolver(uriResolver, "templates");
		final URIResolver contentUriResolver = new CmsInputURIResolver(uriResolver, "contents");
		final OutputURIResolver outputUriResolver = new CmsOutputURIResolver(outputDirectoryUri, outputFileUri);
		
		try {
			final XMLReader reader = readerFactory.createReader();
			final Transformer transformer = createTransformer(templateUriResolver, outputUriResolver);
			
			transformer.setURIResolver(contentUriResolver);
			transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
			transformer.setParameter(RESOURCE_DIRECTORY, relativeBaseUrl + "/r/" + currentResourceDirectoryName);
			transformer.setParameter(SITE_NAME, name);
			transformer.setParameter(PAGE_PATH, pagePath);
			transformer.setParameter(PAGE_TITLE, page.getTitle());
			
			for (IParam param : bundle.getParams())
				transformer.setParameter(SITE_PARAM_PREFIX + param.getId(), param.getValue());
			
			final InputSource source = new InputSource(contentFile.getAbsolutePath());
			final StreamResult result = new StreamResult(outputFile);
			
			transformer.transform(new SAXSource(reader, source), result);
		} catch (TransformerConfigurationException e) {
			throw new RenderingException("Invalid transformer configuration", e);
		} catch (Exception e) {
			throw new RenderingException("Cannot render " + contentFile + ". " + e.getMessage(), e);
		}
	}

	private Transformer createTransformer(final URIResolver uriResolver, final OutputURIResolver outputUriResolver)
			throws TransformerConfigurationException {
		final String xslUri = "/de/algorythm/cms/common/templates/include-view-html.xsl";
		final URL url = getClass().getResource(xslUri);

		if (url == null)
			throw new IllegalStateException("Missing XSL template: " + xslUri);

		final StreamSource xslSource = new StreamSource(url.toString());
		transformerFactory.setURIResolver(uriResolver);
		final Templates tpls = transformerFactory.newTemplates(xslSource);
		final Transformer transformer = tpls.newTransformer();
		((TransformerImpl) transformer).getUnderlyingController().setOutputURIResolver(outputUriResolver);
		
		return transformer;
	}

	private String relativeBaseUrl(final String path) {
		final int depth = pathDepth(path + '/');
		
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
