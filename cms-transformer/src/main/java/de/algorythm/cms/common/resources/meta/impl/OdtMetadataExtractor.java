package de.algorythm.cms.common.resources.meta.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.impl.Metadata;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class OdtMetadataExtractor implements IMetadataExtractor {

	static private Logger log = LoggerFactory.getLogger(CmsMetadataExtractor.class);

	private final IXmlFactory xmlFactory;
	private final IArchiveExtractor archiveExtractor;

	@Inject
	public OdtMetadataExtractor(IXmlFactory xmlFactory, IArchiveExtractor archiveExtractor) {
		this.xmlFactory = xmlFactory;
		this.archiveExtractor = archiveExtractor;
	}

	@Override
	public IMetadata extractMetadata(final URI uri, final IInputResolver resolver, final IWriteableResources tmp)
			throws ResourceNotFoundException, MetadataExtractionException, IOException {
		try {
			final Path extractedOdtDirectory = archiveExtractor.unzip(uri, resolver, tmp);
			final Path metaXmlFile = extractedOdtDirectory.resolve("meta.xml");
			final Metadata md = new Metadata();
			
			if (Files.exists(metaXmlFile)) {
				extractMetadata(metaXmlFile, md);
			} else {
				log.warn("Missing meta.xml in ODT " + uri.getPath());
			}
			
			return md;
		} catch (IOException | XMLStreamException e) {
			throw new MetadataExtractionException("Cannot extract ODT metadata from " + uri.getPath(), e);
		}
	}
	private void extractMetadata(final Path metaXmlFile, final Metadata r) throws IOException, XMLStreamException {
		try (InputStream stream = Files.newInputStream(metaXmlFile)) {
			final XMLEventReader reader = xmlFactory.createXMLEventReader(stream);
			
			try {
				while (reader.hasNext()) {
					XMLEvent evt = reader.nextEvent();
					
					if (!evt.isStartDocument() && evt.isStartElement()) {
						final StartElement element = evt.asStartElement();
						
						if ("title".equals(element.getName().getLocalPart())) {
							if (reader.hasNext()) {
								evt = reader.nextEvent();
								
								if (evt.isCharacters()) {
									final String title = evt.asCharacters().getData().trim();
									
									if (!title.isEmpty())
										r.setTitle(title);
								}
							}
							
							break;
						}
					}
				}
				
				log.warn("Undefined title in " + metaXmlFile);
			} finally {
				reader.close();
			}
		}
	}
}
