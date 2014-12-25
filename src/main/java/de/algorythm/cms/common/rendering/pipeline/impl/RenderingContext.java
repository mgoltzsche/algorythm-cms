package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.SAXException;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISchemaSource;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsOutputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsTemplateURIResolver;
import de.algorythm.cms.common.resources.impl.OutputResolver;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class RenderingContext implements IBundleRenderingContext {

	private final IBundle bundle;
	private final URI resourcePrefix;
	private final Path outputDirectory;
	private final Path tempDirectory;
	private final ISourceUriResolver sourceResolver;
	private final ITargetUriResolver targetResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private final IXmlLoader xmlLoader;
	private final Map<Locale, IPageConfig> localizedPages;

	public RenderingContext(final IBundle bundle, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
		this.sourceResolver = new ResourceResolver(bundle, tempDirectory);
		this.targetResolver = new OutputResolver(outputDirectory, tempDirectory);
		localizedPages = new HashMap<Locale, IPageConfig>();
		final Set<ISchemaSource> locations = new LinkedHashSet<ISchemaSource>(bundle.getSchemaLocations());
		final List<URI> schemaLocations = new LinkedList<URI>();
		
		for (ISchemaSource location : locations)
			schemaLocations.add(location.getUri());
		
		try {
			this.xmlLoader = new XmlLoader(schemaLocations, sourceResolver);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public IPageConfig getStartPage(Locale locale) {
		return localizedPages.get(locale);
	}

	@Override
	public void setStartPage(final Locale locale, final IPageConfig startPage) {
		localizedPages.put(locale, startPage);
	}

	@Override
	public IXmlLoader getXmlLoader() {
		return xmlLoader;
	}

	@Override
	public Path getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public Path getTempDirectory() {
		return tempDirectory;
	}

	@Override
	public ISourceUriResolver getResourceResolver() {
		return sourceResolver;
	}

	@Override
	public ITargetUriResolver getOutputResolver() {
		return targetResolver;
	}

	@Override
	public URI getResourcePrefix() {
		return resourcePrefix;
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public Source getSource(URI uri, Locale locale) throws SAXException, ParserConfigurationException, IOException {
		return xmlLoader.getSource(uri, locale);
	}

	@Override
	public Templates compileTemplates(final Collection<URI> xslSourceUris) {
		final TimeMeter meter = TimeMeter.meter(bundle.getName() + " template compilation");
		final Source xslSource = createMergedXslSource(xslSourceUris);
		final TransformerFactory transformerFactory = SAXTransformerFactory.newInstance();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		final Templates templates;
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(new CmsTemplateURIResolver(sourceResolver));
		
		try {
			templates = transformerFactory.newTemplates(xslSource);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot load XSL templates. " + errorListener, e);
		}
		
		errorListener.evaluateErrors();
		meter.finish();
		
		return templates;
	}

	@Override
	public Transformer createTransformer(final Templates templates, final URI notFoundContent, final Locale locale) throws TransformerConfigurationException {
		final Transformer transformer = templates.newTransformer();
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final URIResolver uriResolverAdapter = new CmsInputURIResolver(this, locale, notFoundContent);
		final OutputURIResolver outputUriResolverAdapter = new CmsOutputURIResolver(targetResolver, locale);
		
		transformer.setURIResolver(uriResolverAdapter);
		trnsfrmCtrl.setOutputURIResolver(outputUriResolverAdapter);
		
		return transformer;
	}

	@Override
	public void transform(URI sourceUri, URI targetUri, Transformer transformer, Locale locale) throws IOException, TransformerException {
		final Source source;
		
		try {
			source = getSource(sourceUri, locale);
		} catch (SAXException | ParserConfigurationException e) {
			throw new TransformerException("Cannot load " + sourceUri, e);
		}
		
		transform(source, targetUri, transformer, locale);
	}
	
	@Override
	public void transform(Source source, URI targetUri, Transformer transformer, Locale locale) throws IOException, TransformerException {
		final TimeMeter meter = TimeMeter.meter(bundle.getName() + " transform " + source.getSystemId());
		final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
		final Path outputFile = targetResolver.resolveUri(targetUri, locale);
		Files.createDirectories(outputFile.getParent());
		final Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
		final StreamResult result = new StreamResult(writer);
		final String targetUriStr = targetUri.toString();
		final TemplateErrorListener errorListener = new TemplateErrorListener();
		
		result.setSystemId(targetUriStr);
		trnsfrmCtrl.setBaseOutputURI(targetUriStr);
		transformer.setErrorListener(errorListener);
		transformer.transform(source, result);
		errorListener.evaluateErrors();
		meter.finish();
	}

	private Source createMergedXslSource(final Collection<URI> xslSourceUris) {
		if (xslSourceUris.size() == 1) {
			final URI sourceUri = xslSourceUris.iterator().next();
			final Path sourcePath = sourceResolver.resolve(sourceUri, Locale.ROOT);
			final InputStream stream;
			
			try {
				stream = Files.newInputStream(sourcePath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			final Source source = new StreamSource(stream, sourceUri.toString());
			
			return source;
		}
		
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (URI sourceUri : xslSourceUris) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(sourceUri.toString()))
				.append("\" />");
		}
		
		final String mergedXsl = xslt.append("</xsl:stylesheet>").toString();
		final Reader mergedTplReader = new StringReader(mergedXsl);
		final StreamSource source = new StreamSource(mergedTplReader);
		
		return source;
	}
}