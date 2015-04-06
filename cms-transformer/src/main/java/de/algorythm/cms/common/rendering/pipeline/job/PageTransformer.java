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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.rendering.pipeline.impl.TemplateErrorListener;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.SimpleLocator;

@Singleton
public class PageTransformer {

	private final IXmlFactory xmlFactory;

	@Inject
	public PageTransformer(final IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void transformPage(final URI sourceUri, final Locale locale,
			final String path, final Map<String, String> params, 
			final Templates templates, final IRenderingContext ctx,
			final String outputFileName,
			final IOutputTargetFactory targetFactory)
					throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException, ResourceNotFoundException {
		final String relativeRootPath = relativeRootPath(path);
		final String resourceBaseUrl = URI.create(relativeRootPath + ctx.getResourcePrefix()).normalize().toString();
		final String outputPath = path + '/' + outputFileName;
		final TransformerHandler transformerHandler = xmlFactory.createTransformerHandler(templates, ctx, outputPath, targetFactory);
		final Transformer transformer = transformerHandler.getTransformer();
		final TemplateErrorListener errorListener = new TemplateErrorListener(sourceUri.toString());
		
		transformer.setErrorListener(errorListener);
		transformer.setParameter(RELATIVE_BASE_URL, relativeRootPath);
		transformer.setParameter(RESOURCE_BASE_URL, resourceBaseUrl);
		transformer.setParameter(SITE_NAME, ctx.getName());
		transformer.setParameter(SITE_INTERNATIONALIZED, true);
		transformer.setParameter(PAGE_PATH, path);
		transformer.setParameter(PAGE_LANGUAGE, locale.getLanguage());
		transformer.setParameter(PAGE_COUNTRY, locale.getCountry());
		transformer.setParameter(PAGE_LOCALE, locale.toLanguageTag());
		
		for (Entry<String, String> param : params.entrySet())
			transformer.setParameter(SITE_PARAM_PREFIX + param.getKey(), param.getValue());
		
		transformerHandler.setSystemId("file:///pages/" + path);
		
		transformPage(sourceUri, transformerHandler);
		errorListener.evaluateErrors();
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

	private String relativeRootPath(final String path) {
		final StringBuilder sb = new StringBuilder(".");
		
		for (int i = 0; i < path.length(); i++)
			if (path.charAt(i) == '/')
				sb.append("/..");
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return "XsltRenderer";
	}
}
