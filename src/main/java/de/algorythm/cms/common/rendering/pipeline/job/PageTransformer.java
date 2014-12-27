package de.algorythm.cms.common.rendering.pipeline.job;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_COUNTRY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_LANGUAGE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_LOCALE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_TITLE;
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

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.PageConfig;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class PageTransformer implements IRenderingJob {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";

	@Inject
	private JAXBContext jaxbContext;
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
		
		for (ISupportedLocale supportedLocale : supportedLocales) {
			final Locale locale = supportedLocale.getLocale();
			final IPageConfig startPage = ctx.getStartPage(locale);
			
			renderPages(startPage, templates, ctx, locale, localizedResourceBasePath);
		}
		
		meter.finish();
	}
	
	private void renderPages(final IPageConfig pageConfig, final Templates compiledTemplates, final IRenderingContext ctx, final Locale locale, final String resourceBasePath) {
		ctx.execute(new IRenderingJob() {
			@Override
			public void run(final IRenderingContext ctx) throws Exception {
				render(ctx, pageConfig, compiledTemplates, locale, resourceBasePath);
			}
			@Override
			public String toString() {
				return pageConfig.getPath() + "/page.xml";
			}
		});
		
		// Render sub pages
		for (IPageConfig child : pageConfig.getPages())
			renderPages(child, compiledTemplates, ctx, locale, resourceBasePath);
	}
	
	private void render(final IRenderingContext ctx, final IPageConfig pageCfg, final Templates compiledTemplates, final Locale locale, final String resourceBasePath) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException {
		final IBundle bundle = ctx.getBundle();
		final String pagePath = pageCfg.getPath();
		final URI targetUri = ctx.getBundle().getSupportedLocales().size() > 1
				? URI.create('/' + locale.getLanguage() + pageCfg.getPath() + "/index.html")
				: URI.create(pageCfg.getPath() + "/index.html");
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final String resourceBaseUrl = URI.create(relativeBaseUrl + resourceBasePath).normalize().toString();
		final TransformerHandler transformerHandler = ctx.createTransformerHandler(compiledTemplates, targetUri);
		final Transformer transformer = transformerHandler.getTransformer();
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
		transformer.setParameter(RESOURCE_BASE_URL, resourceBaseUrl);
		transformer.setParameter(SITE_NAME, bundle.getName());
		transformer.setParameter(SITE_INTERNATIONALIZED, bundle.getSupportedLocales().size() > 1);
		transformer.setParameter(PAGE_PATH, pagePath);
		transformer.setParameter(PAGE_TITLE, pageCfg.getTitle());
		transformer.setParameter(PAGE_LANGUAGE, locale.getLanguage());
		transformer.setParameter(PAGE_COUNTRY, locale.getCountry());
		transformer.setParameter(PAGE_LOCALE, locale.toLanguageTag());
		
		for (IParam param : bundle.getParams())
			transformer.setParameter(SITE_PARAM_PREFIX + param.getId(), param.getValue());
		
		marshaller.marshal(createPageFeed(pageCfg), transformerHandler);
	}
	
	private IPageConfig createPageFeed(final IPageConfig page) {
		PageConfig p = new PageConfig(page.getPath(), page.getName());
		p.setTitle(page.getTitle());
		p.setNavigationTitle(page.getNavigationTitle());
		p.setInNavigation(page.isInNavigation());
		p.setContent(page.getContent());
		return p;
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
	
	@Override
	public String toString() {
		return "XsltRenderer";
	}
}
