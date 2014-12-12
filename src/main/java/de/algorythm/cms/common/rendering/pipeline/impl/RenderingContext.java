package de.algorythm.cms.common.rendering.pipeline.impl;

import static de.algorythm.cms.common.rendering.pipeline.impl.TransformationContextInitializationUtil.ERROR_HANDLER;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISchemaLocation;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.impl.OutputResolver;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class RenderingContext implements IBundleRenderingContext {

	private final IBundle bundle;
	private final URI resourcePrefix;
	private final Path outputDirectory;
	private final Path tempDirectory;
	private final IUriResolver resourceResolver;
	private final IOutputUriResolver outputResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private SAXParserFactory saxParserFactory;
	private final IXmlLoader xmlLoader;

	public RenderingContext(final IBundle bundle, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
		this.resourceResolver = new ResourceResolver(bundle, tempDirectory);
		this.outputResolver = new OutputResolver(outputDirectory);
		final Set<ISchemaLocation> locations = new LinkedHashSet<ISchemaLocation>(bundle.getSchemaLocations());
		final List<URI> schemaLocations = new LinkedList<URI>();
		
		for (ISchemaLocation location : locations)
			schemaLocations.add(location.getUri());
		
		try {
			this.xmlLoader = new XmlDomLoader(schemaLocations, resourceResolver);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public RenderingContext(final IBundle bundle, final IXmlLoader xmlLoader, final IUriResolver uriResolver, final IOutputUriResolver outUriResolver, final Path tempDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.xmlLoader = xmlLoader;
		this.resourceResolver = uriResolver;
		this.outputResolver = outUriResolver;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
	}

	@Override
	public IBundleRenderingContext createLocalized(final Locale locale, boolean localizeOutput) {
		final IUriResolver uriResolver = resourceResolver.createLocalizedResolver(locale);
		final IOutputUriResolver outUriResolver = localizeOutput
				? outputResolver.createLocalizedResolver(locale)
				: outputResolver;
		return new RenderingContext(bundle, xmlLoader, uriResolver, outUriResolver, tempDirectory, outputDirectory, resourcePrefix);
	}
	
	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public XMLReader createXmlReader() {
		try {
			final XMLReader r = saxParserFactory.newSAXParser().getXMLReader();
			
			r.setErrorHandler(ERROR_HANDLER);
			
			return r;
		} catch(Exception e) {
			throw new IllegalStateException("Cannot create SAX parser. " + e, e);
		}
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
	public IUriResolver getResourceResolver() {
		return resourceResolver;
	}

	@Override
	public IOutputUriResolver getOutputResolver() {
		return outputResolver;
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
	public Document getDocument(URI uri) {
		final Path path = resourceResolver.resolve(uri);
		
		return xmlLoader.loadDocument(path);
	}

	@Override
	public Document transform(URI source, URI target, Transformer transformer) {
		final Document sourceDoc = getDocument(source);
		
		final Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
		final StreamResult result = new StreamResult(writer);
		
		return transformer.transform(new DOMSource(document, ), outputTarget)
	}
}
