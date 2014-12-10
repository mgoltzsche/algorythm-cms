package de.algorythm.cms.common.rendering.pipeline.impl;

import static de.algorythm.cms.common.rendering.pipeline.impl.TransformationContextInitializationUtil.ERROR_HANDLER;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISchemaLocation;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.XsdResourceResolver;
import de.algorythm.cms.common.resources.impl.OutputResolver;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class RenderingContext implements IBundleRenderingContext {

	private final IBundle bundle;
	private final Path resourcePrefix;
	private final Path outputDirectory;
	private final Path tempDirectory;
	private final IUriResolver resourceResolver;
	private final IOutputUriResolver outputResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private SAXParserFactory saxParserFactory;

	public RenderingContext(final IBundle bundle, final Path tempDirectory, final Path outputDirectory, final Path resourcePrefix) {
		this.bundle = bundle;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
		this.resourceResolver = new ResourceResolver(bundle, tempDirectory);
		this.outputResolver = new OutputResolver(outputDirectory);
		
		final Set<ISchemaLocation> locations = new LinkedHashSet<ISchemaLocation>(bundle.getSchemaLocations());
		final List<Path> schemaLocations = new LinkedList<Path>();
		
		for (ISchemaLocation location : locations)
			schemaLocations.add(resourceResolver.resolve(Paths.get(location.getUri().getPath())));
		
		try {
			saxParserFactory = createParserFactory(schemaLocations);
		} catch (IOException e) {
			throw new RuntimeException("Cannot create schema");
		}
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
	
	private SAXParserFactory createParserFactory(final Collection<Path> schemaLocations) throws IOException {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		final Schema schema = createSchema(schemaLocations);
		
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema); // REQUIRED FOR VALIDATION ONLY
		
		return parserFactory;
	}
	
	private Schema createSchema(final Collection<Path> schemaLocations) throws IOException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Source[] sources = new Source[schemaLocations.size()];
		int i = 0;
		
		schemaFactory.setResourceResolver(new XsdResourceResolver(resourceResolver));
		
		for (Path schemaLocation : schemaLocations) {
			final Reader fileReader = Files.newBufferedReader(schemaLocation, StandardCharsets.UTF_8);
			final Source source = new StreamSource(fileReader);
			
			source.setSystemId(schemaLocation.toString());
			
			sources[i++] = source;
		}
		
		try {
			return schemaFactory.newSchema(sources);
		} catch(SAXException e) {
			throw new IllegalStateException("Cannot load XML schema. " + e, e);
		}
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
	public Path getResourcePrefix() {
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
}
