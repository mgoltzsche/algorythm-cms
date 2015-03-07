package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISchemaSource;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IDestinationPathResolver;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.OutputResolver;
import de.algorythm.cms.common.resources.impl.ResourceResolver;
import de.algorythm.cms.common.resources.impl.SynchronizedZipArchiveExtractor;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class RenderingContext implements IBundleRenderingContext {

	private final IBundle bundle;
	private final URI resourcePrefix;
	private final ISourcePathResolver sourceResolver;
	private final IDestinationPathResolver targetResolver;
	private final IXmlSourceResolver xmlSourceResolver;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());
	private final XmlContext xmlContext;
	private final IArchiveExtractor archiveExtractor;
	private final JAXBContext jaxbContext;
	private final Path tempDirectory;
	private final IMetadataExtractor metadata;
	private Collection<IPageConfig> renderPages;

	public RenderingContext(final IBundle bundle, final IMetadataExtractor metadata, final JAXBContext jaxbContext, final IXmlSourceResolver xmlSourceResolver, final Path tmpDirectory, final Path outputDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.metadata = metadata;
		this.resourcePrefix = resourcePrefix;
		this.jaxbContext = jaxbContext;
		this.xmlSourceResolver = xmlSourceResolver;
		this.tempDirectory = tmpDirectory;
		this.sourceResolver = new ResourceResolver(bundle, tmpDirectory);
		this.targetResolver = new OutputResolver(outputDirectory, tmpDirectory);
		this.archiveExtractor = new SynchronizedZipArchiveExtractor(this, tmpDirectory);
		final Set<ISchemaSource> locations = new LinkedHashSet<ISchemaSource>(bundle.getSchemaLocations());
		final List<URI> schemaLocations = new LinkedList<URI>();
		
		for (ISchemaSource location : locations)
			schemaLocations.add(location.getUri());
		
		try {
			this.xmlContext = new XmlContext(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Collection<IPageConfig> getRenderPages() {
		return renderPages;
	}

	public void setRenderPages(Collection<IPageConfig> renderPages) {
		this.renderPages = renderPages;
	}

	@Override
	public IBundle getBundle() {
		return bundle;
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
	public Path getTempDirectory() {
		return tempDirectory;
	}
	@Override
	public Marshaller createMarshaller() throws JAXBException {
		return jaxbContext.createMarshaller();
	}

	@Override
	public void parse(URI publicUri, ContentHandler handler)
			throws IOException, SAXException, ParserConfigurationException, ResourceNotFoundException {
		xmlContext.parse(publicUri, handler);
	}

	@Override
	public Templates compileTemplates(Collection<URI> xslSourceUris)
			throws TransformerConfigurationException {
		return xmlContext.compileTemplates(xslSourceUris);
	}

	@Override
	public Templates compileTemplates(URI xslSourceUri)
			throws TransformerConfigurationException, ResourceNotFoundException {
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

	@Override
	public Source createXmlSource(URI uri) throws ResourceNotFoundException, IOException {
		return xmlSourceResolver.createXmlSource(uri, this);
	}

	@Override
	public Path resolveSource(URI uri) throws ResourceNotFoundException {
		return sourceResolver.resolveSource(uri);
	}

	@Override
	public Path resolveDestination(URI uri) {
		return targetResolver.resolveDestination(uri);
	}

	@Override
	public Path unzip(URI uri) throws ResourceNotFoundException, IOException {
		return archiveExtractor.unzip(uri);
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream stream)
			throws XMLStreamException {
		return xmlContext.createXMLEventReader(stream);
	}

	@Override
	public IMetadata extractMetadata(URI uri) throws ResourceNotFoundException, MetadataExtractionException {
		return metadata.extractMetadata(uri, this);
	}
}