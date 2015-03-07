package de.algorythm.cms.common.rendering.pipeline.job;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_COUNTRY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_LANGUAGE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_LOCALE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RELATIVE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_INTERNATIONALIZED;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_NAME;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_PARAM_PREFIX;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.impl.TemplateErrorListener;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.SimpleLocator;

public class PageTransformer implements IRenderingJob {

	static private final String DOT = ".";

	private List<URI> templates = new LinkedList<URI>();
	private URI theme;
	
	@Override
	public void run(final IRenderingContext ctx) throws TransformerConfigurationException {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this + " initialization");
		final LinkedList<URI> templateLocations = new LinkedList<URI>();
		
		templateLocations.addAll(templates);
		templateLocations.add(theme);
		
		final IBundle bundle = ctx.getBundle();
		final Set<ISupportedLocale> supportedLocales = bundle.getSupportedLocales();
		final boolean localizeOutput = supportedLocales.size() > 1;
		String resourceBasePath = ctx.getResourcePrefix().toString();
		
		if (!resourceBasePath.isEmpty() && resourceBasePath.charAt(resourceBasePath.length() - 1) == '/')
			resourceBasePath = resourceBasePath.substring(0, resourceBasePath.length() - 1);
		
		final String localizedResourceBasePath = localizeOutput
				? "/.." + resourceBasePath
				: resourceBasePath;
		final Templates templates = ctx.compileTemplates(templateLocations);
		final IPageConfig startPage = bundle.getStartPage();
		
		for (ISupportedLocale supportedLocale : supportedLocales) {
			final Locale locale = supportedLocale.getLocale();
			
			renderPages(startPage, StringUtils.EMPTY, DOT, templates, ctx, locale, localizedResourceBasePath);
		}
		
		meter.finish();
	}
	
	private void renderPages(final IPageConfig pageConfig, final String path, final String relativeRootPath, final Templates compiledTemplates, final IRenderingContext ctx, final Locale locale, final String resourceBasePath) {
		ctx.execute(new IRenderingJob() {
			@Override
			public void run(final IRenderingContext ctx) throws Exception {
				renderPage(ctx, pageConfig.getSource(), path, relativeRootPath, compiledTemplates, locale, resourceBasePath);
			}
			@Override
			public String toString() {
				return path + '/';
			}
		});
		
		// Render sub pages
		for (IPageConfig child : pageConfig.getPages())
			renderPages(child, path + '/' + child.getName(), relativeRootPath + "/..", compiledTemplates, ctx, locale, resourceBasePath);
	}
	
	private void renderPage(final IRenderingContext ctx, final URI sourceUri, final String path, final String relativeRootPath, final Templates compiledTemplates, final Locale locale, final String resourceBasePath) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException, ResourceNotFoundException {
		final IBundle bundle = ctx.getBundle();
		final boolean internationalized = ctx.getBundle().getSupportedLocales().size() > 1;
		final URI targetUri = internationalized
				? URI.create('/' + locale.getLanguage() + path + "/index.html")
				: URI.create(path + "/index.html");
		final String resourceBaseUrl = URI.create(relativeRootPath + resourceBasePath).normalize().toString();
		final TransformerHandler transformerHandler = ctx.createTransformerHandler(compiledTemplates, targetUri);
		final Transformer transformer = transformerHandler.getTransformer();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		
		transformer.setErrorListener(errorListener);
		transformer.setParameter(RELATIVE_BASE_URL, relativeRootPath);
		transformer.setParameter(RESOURCE_BASE_URL, resourceBaseUrl);
		transformer.setParameter(SITE_NAME, bundle.getName());
		transformer.setParameter(SITE_INTERNATIONALIZED, internationalized);
		transformer.setParameter(PAGE_PATH, path);
		transformer.setParameter(PAGE_LANGUAGE, locale.getLanguage());
		transformer.setParameter(PAGE_COUNTRY, locale.getCountry());
		transformer.setParameter(PAGE_LOCALE, locale.toLanguageTag());
		
		for (IParam param : bundle.getParams())
			transformer.setParameter(SITE_PARAM_PREFIX + param.getId(), param.getValue());
		
		transformerHandler.setSystemId("file:///pages/" + path);
		
		transformPage(sourceUri, transformerHandler);
	}

	private void transformPage(final URI uri, final TransformerHandler transformerHandler) throws SAXException {
		final String ns = "http://cms.algorythm.de/common/Page";
		final String lName = "page";
		final String qName = "p:page";
		final String prefix = "p";
		final AttributesImpl atts = new AttributesImpl();
		
		atts.addAttribute(StringUtils.EMPTY, "content", "content", "anyUri", uri.toString());
		
		transformerHandler.setDocumentLocator(SimpleLocator.INSTANCE);
		transformerHandler.startDocument();
		transformerHandler.startPrefixMapping(prefix, ns);
		transformerHandler.startElement(ns, lName, qName, atts);
		transformerHandler.endElement(ns, lName, qName);
		transformerHandler.endPrefixMapping(prefix);
		transformerHandler.endDocument();
	}

	@Override
	public String toString() {
		return "XsltRenderer";
	}
}
