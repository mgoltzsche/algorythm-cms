package de.algorythm.cms.common.rendering.pipeline.job;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_TITLE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RELATIVE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_NAME;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_PARAM_PREFIX;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.Controller;
import net.sf.saxon.jaxp.TransformerImpl;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.impl.RenderingException;
import de.algorythm.cms.common.rendering.pipeline.impl.TransformationContext;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;

public class XsltRenderer implements IRenderingJob {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";
	
	@Inject
	private IBundleLoader loader;
	private List<URI> templates = new LinkedList<URI>();
	private URI theme;
	private URI notFoundContent;
	
	@Override
	public void run(final IRenderingContext ctx) {
		final LinkedList<URI> tpls = new LinkedList<URI>();
		
		tpls.addAll(templates);
		tpls.add(theme);
		
		final IBundle bundle = ctx.getBundle();
		final TransformationContext transformCtx = new TransformationContext(ctx, tpls, notFoundContent);
		final URI resourceBasePath = ctx.getResourcePrefix();
		final Set<ISupportedLocale> supportedLocales = bundle.getSupportedLocales();
		final boolean localizeOutput = supportedLocales.size() > 1;
		final URI localizedResourceBasePath = localizeOutput
				? URI.create("/.." + resourceBasePath.getPath())
				: resourceBasePath;
		
		for (ISupportedLocale supportedLocale : supportedLocales) {
			final Locale locale = supportedLocale.getLocale();
			final TransformationContext localizedTransformCtx = transformCtx.createLocalized(locale, localizeOutput);
			final IPage startPage = loader.loadPages(bundle, locale);
			
			renderPages(startPage, ctx, localizedTransformCtx, localizedResourceBasePath);
		}
	}
	
	private void renderPages(final IPage page, final IRenderingContext ctx, final TransformationContext transformCtx, final URI resourceBasePath) {
		ctx.execute(new IRenderingJob() {
			@Override
			public void run(final IRenderingContext ctx) throws Exception {
				render(ctx.getBundle(), page, ctx, transformCtx, resourceBasePath);
			}
			@Override
			public String toString() {
				return page.getPath() + "/page.xml";
			}
		});
		
		// Render sub pages
		for (IPage child : page.getPages())
			renderPages(child, ctx, transformCtx, resourceBasePath);
	}
	
	private void render(final IBundle bundle, final IPage page, final IRenderingContext ctx, final TransformationContext transformCtx, final URI resourceBasePath) throws RenderingException, IOException {
		final IUriResolver uriResolver = ctx.getResourceResolver();
		final URI pageUri = URI.create("/pages/" + page.getPath());
		final Path systemPagePath = uriResolver.resolve(pageUri);
		final Reader fileReader = Files.newBufferedReader(systemPagePath, StandardCharsets.UTF_8);
		final InputSource src = new InputSource(fileReader);
		final Source pageSource = new SAXSource(xmlReader, src);
		pageSource.setSystemId(pageUri.toString());
		final String name = bundle.getName();
		final String pagePath = page.getPath();
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final URI outputUri = URI.create(pagePath + "/index.html");
		final IOutputUriResolver outputResolver = transformCtx.getOutputUriResolver();
		final Path outputFile = outputResolver.resolveUri(outputUri);
		Files.createDirectories(outputFile.getParent());
		final Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
		final StreamResult result = new StreamResult(writer);
		final Transformer transformer = transformCtx.createTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		
		result.setSystemId(outputFile.toString());
		trnsfrmCtrl.setBaseOutputURI(outputFile.toString());
		transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
		transformer.setParameter(RESOURCE_BASE_URL, relativeBaseUrl + resourceBasePath);
		transformer.setParameter(SITE_NAME, name);
		transformer.setParameter(PAGE_PATH, pagePath);
		transformer.setParameter(PAGE_TITLE, page.getTitle());
		
		for (IParam param : bundle.getParams())
			transformer.setParameter(SITE_PARAM_PREFIX + param.getId(), param.getValue());
		
		try {
			transformer.transform(pageSource, result);
		} catch (TransformerException e) {
			throw new RuntimeException("Cannot transform " + page.getPath() + "/. " + e.getMessage(), e);
		}
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
