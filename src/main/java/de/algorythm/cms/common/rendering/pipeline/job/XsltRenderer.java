package de.algorythm.cms.common.rendering.pipeline.job;

import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_PATH;
import static de.algorythm.cms.common.ParameterNameConstants.Render.PAGE_TITLE;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RELATIVE_BASE_URL;
import static de.algorythm.cms.common.ParameterNameConstants.Render.RESOURCE_DIRECTORY;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_NAME;
import static de.algorythm.cms.common.ParameterNameConstants.Render.SITE_PARAM_PREFIX;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

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
import de.algorythm.cms.common.resources.impl.CmsOutputURIResolver;

public class XsltRenderer implements IRenderingJob {

	static private final String BACK_SLASH = "../";
	static private final String BACK = "..";
	static private final String DOT = ".";
		
	@Inject
	private IBundleLoader loader;
	private URI theme;
	private List<URI> templates = new LinkedList<URI>();
	private List<URI> schemas = new LinkedList<URI>();
	
	@Override
	public void run(final IRenderingContext ctx) throws FileNotFoundException {
		final LinkedList<URI> tpls = new LinkedList<URI>();
		
		tpls.add(theme);
		tpls.addAll(templates);
		
		final IBundle bundle = ctx.getBundle();
		final TransformationContext transformCtx = new TransformationContext(ctx, schemas, tpls);
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			final TransformationContext localizedTransformCtx = transformCtx.createLocalized(locale);
			final IPage startPage = loader.loadPages(bundle, locale);
			
			renderPages(startPage, localizedTransformCtx, ctx.getResourcePrefix(), ctx.getOutputDirectory().toURI());
		}
	}
	
	private void renderPages(final IPage page, final TransformationContext transformCtx, final String outputResourceDirectory, final URI outputDirectoryUri) {
		transformCtx.execute(new IRenderingJob() {
			@Override
			public void run(final IRenderingContext ctx) throws Exception {
				render(ctx.getBundle(), page, transformCtx, outputResourceDirectory, outputDirectoryUri);
			}
			@Override
			public String toString() {
				return page.getPath() + "/page.xml";
			}
		});
		
		// Render sub pages
		for (IPage child : page.getPages())
			renderPages(child, transformCtx, outputResourceDirectory, outputDirectoryUri);
	}
	
	private void render(final IBundle bundle, final IPage page, final TransformationContext ctx, final String outputResourceDirectory, final URI outputDirectoryUri) throws RenderingException {
		final XMLReader reader = ctx.createReader();
		final URI pageUri = bundle.getLocation().resolve("international/pages" + page.getPath() + "/page.xml");
		final InputSource src = new InputSource(pageUri.getPath());
		final Source pageSource = new SAXSource(reader, src);
		final String name = bundle.getName();
		final String pagePath = page.getPath();
		final String relativeBaseUrl = relativeBaseUrl(pagePath);
		final URI outputFileUri = ctx.getOutputUriResolver().resolveUri(URI.create(page.getPath() + "/index.html"));
		final File outputFile = new File(outputFileUri);
		final OutputURIResolver outputUriResolver = new CmsOutputURIResolver(outputDirectoryUri, outputFileUri);
		final StreamResult result = new StreamResult(outputFile);
		final Transformer transformer = ctx.createTransformer();
		
		((TransformerImpl) transformer).getUnderlyingController().setOutputURIResolver(outputUriResolver);
		transformer.setParameter(RELATIVE_BASE_URL, relativeBaseUrl);
		transformer.setParameter(RESOURCE_DIRECTORY, relativeBaseUrl + outputResourceDirectory);
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
}
