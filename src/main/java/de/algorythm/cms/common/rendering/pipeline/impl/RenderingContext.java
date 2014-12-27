package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;
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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISchemaSource;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlContext;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;
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
	private final XmlContext xmlContext;
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
			this.xmlContext = new XmlContext(sourceResolver, targetResolver);
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
	public IXmlContext getXmlLoader() {
		return xmlContext;
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
	public Source getSource(URI publicUri) throws SAXException,
			ParserConfigurationException, IOException {
		return xmlContext.getSource(publicUri);
	}

	@Override
	public void parse(URI publicUri, ContentHandler handler)
			throws IOException, SAXException, ParserConfigurationException {
		xmlContext.parse(publicUri, handler);
	}

	@Override
	public Templates compileTemplates(Collection<URI> xslSourceUris)
			throws TransformerConfigurationException {
		return xmlContext.compileTemplates(xslSourceUris);
	}

	@Override
	public Templates compileTemplates(URI xslSourceUri)
			throws TransformerConfigurationException {
		return xmlContext.compileTemplates(xslSourceUri);
	}

	@Override
	public TransformerHandler createTransformerHandler(Templates templates,
			URI outputUri)
			throws IOException, TransformerConfigurationException {
		return xmlContext.createTransformerHandler(templates, outputUri);
	}

	@Override
	public XMLReader createXMLReader() throws SAXException {
		return xmlContext.createXMLReader();
	}

	@Override
	public ContentHandler createXMLWriter(URI publicUri)
			throws IOException, TransformerConfigurationException {
		return xmlContext.createXMLWriter(publicUri);
	}

	@Override
	public XMLFilter createXMLFilter(Templates templates, XMLReader parent)
			throws TransformerConfigurationException {
		return xmlContext.createXMLFilter(templates, parent);
	}
}