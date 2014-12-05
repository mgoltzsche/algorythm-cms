package de.algorythm.cms.common.rendering.pipeline.job;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_TITLE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RELATIVE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_DIRECTORY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_NAME;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_PARAM_PREFIX;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.renderer.RenderingException;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.resources.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.impl.CmsOutputURIResolver;
import de.algorythm.cms.common.resources.impl.XsdResourceResolver;

public class XsltRenderer implements IRenderingJob {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";
	
	static private class SaxErrorHandler implements ErrorHandler {

		@Override
		public void warning(final SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void error(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}

		@Override
		public void fatalError(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}
	}
	
	@Inject
	private IBundleLoader loader;
	private URI theme;
	private List<URI> templates = new LinkedList<URI>();
	private List<File> schemas = new LinkedList<File>();
	
	@Override
	public void run(final IRenderingContext ctx) {
		final IBundle bundle = ctx.getBundle();
		final IResourceResolver uriResolver = ctx.getInputUriResolver();
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			final IResourceResolver localizedUriResolver = uriResolver.createLocalizedResolver(locale);
			final SAXParserFactory parserFactory = createSAXParserFactory(uriResolver);
			final Templates templates = createTransformationTemplates(localizedUriResolver);
			final IPage startPage = loader.loadPages(bundle, locale);
			
			renderPages(startPage, ctx, parserFactory, templates);
		}
	}
	
	private void renderPages(final IPage page, final IRenderingContext ctx, final SAXParserFactory parserFactory, final Templates templates) {
		final XMLReader reader;
		
		try {
			reader = parserFactory.newSAXParser().getXMLReader();
		} catch(Exception e) {
			throw new IllegalStateException("Cannot create SAX parser. " + e, e);
		}
		
		final Transformer transformer;
		
		try {
			transformer = templates.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot create transformer. " + e, e);
		}
		
		reader.setErrorHandler(new SaxErrorHandler());
		ctx.execute(new IRenderingJob() {
			@Override
			public void run(final IRenderingContext ctx) throws Exception {
				final URI pageUri = ctx.getBundle().getLocation().resolve("international/pages" + page.getPath() + "/page.xml");
				final InputSource src = new InputSource(pageUri.getPath());
				final Source pageSource = new SAXSource(reader, src);
				
				render(pageSource, page, ctx, reader, transformer);
			}
			@Override
			public String toString() {
				return page.getPath() + "/page.xml";
			}
		});
		
		// Render sub pages
		for (IPage child : page.getPages())
			renderPages(child, ctx, parserFactory, templates);
	}
	
	private void render(final Source source, final IPage page, final IRenderingContext ctx, final XMLReader reader, final Transformer transformer) throws RenderingException {
		final IBundle bundle = ctx.getBundle();
		final URI outputDirectoryUri = ctx.getOutputDirectory().toURI();
		final String name = bundle.getName();
		final String pagePath = page.getPath();
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final String outputDirUriStr = outputDirectoryUri.toString();
		final URI outputFileUri = URI.create(outputDirUriStr.substring(0, outputDirUriStr.length() - 1) + page.getPath() + "/index.html");
		final File outputFile = new File(outputFileUri);
		final OutputURIResolver outputUriResolver = new CmsOutputURIResolver(outputDirectoryUri, outputFileUri);
		final StreamResult result = new StreamResult(outputFile);
		
		((TransformerImpl) transformer).getUnderlyingController().setOutputURIResolver(outputUriResolver);
		transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
		transformer.setParameter(RESOURCE_DIRECTORY, relativeBaseUrl + ctx.getResourcePrefix());
		transformer.setParameter(SITE_NAME, name);
		transformer.setParameter(PAGE_PATH, pagePath);
		transformer.setParameter(PAGE_TITLE, page.getTitle());
		
		for (IParam param : bundle.getParams())
			transformer.setParameter(SITE_PARAM_PREFIX + param.getId(), param.getValue());
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new RuntimeException("Cannot transform " + page.getPath() + "/. " + e.getMessage(), e);
		}
	}
	
	private SAXParserFactory createSAXParserFactory(final IResourceResolver uriResolver) {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final Schema schema = createSchema(uriResolver);
		
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema); // REQUIRED FOR VALIDATION ONLY
		
		return parserFactory;
	}
	
	private Schema createSchema(final IResourceResolver uriResolver) {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemas.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new XsdResourceResolver(uriResolver));
		
		for (File schemaFile : schemas)
			sources[i++] = new StreamSource(schemaFile);
		
		try {
			return schemaFactory.newSchema(sources);
			//schema = schemaFactory.newSchema(new StreamSource(new File("/home/max/development/java/algorythm-cms/target/classes/de/algorythm/cms/common/types/CMS.xsd")));
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML schema. " + e, e);
		}
	}
	
	private Templates createTransformationTemplates(final IResourceResolver uriResolver) {
		final URIResolver uriResolverAdapter = new CmsInputURIResolver(uriResolver, "templates");
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Reader mergedTplReader = new StringReader(createMergedTemplate());
		final Source xslSource = new StreamSource(mergedTplReader);
		
		transformerFactory.setURIResolver(uriResolverAdapter);
		
		try {
			return transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot load XSL templates. " + e, e);
		}
	}
	
	private String createMergedTemplate() {
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		if (theme != null)
			appendXslImportTag(theme, xslt);
		
		for (URI templateUri : templates)
			appendXslImportTag(templateUri, xslt);
		
		return xslt.append("</xsl:stylesheet>").toString();
	}
	
	private void appendXslImportTag(final URI xslPublicUri, final StringBuilder xslt) {
		xslt.append("\n<xsl:import href=\"").append(StringEscapeUtils.escapeXml(xslPublicUri.getPath())).append("\" />");
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
