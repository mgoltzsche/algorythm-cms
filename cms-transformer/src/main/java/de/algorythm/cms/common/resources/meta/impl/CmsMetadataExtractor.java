package de.algorythm.cms.common.resources.meta.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.impl.Metadata;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IInputSource;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class CmsMetadataExtractor implements IMetadataExtractor {

	static private Logger log = LoggerFactory.getLogger(CmsMetadataExtractor.class);
	
	private final IXmlFactory xmlFactory;

	@Inject
	public CmsMetadataExtractor(final IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}
	
	@Override
	public IMetadata extractMetadata(final URI uri, final IInputResolver resolver, final IWriteableResources tmp) throws ResourceNotFoundException, MetadataExtractionException, IOException {
		final IInputSource source = resolver.resolveResource(uri);
		
		if (source == null)
			throw new ResourceNotFoundException("Cannot find XML file for metadata extraction at " + uri);
		
		final Metadata m = new Metadata(source.getCreationTime(), source.getLastModifiedTime());
		
		try (InputStream stream = source.createInputStream()) {
			final XMLEventReader reader;
			
			try {
				reader = xmlFactory.createXMLEventReader(stream);
			} catch (XMLStreamException e1) {
				throw new MetadataExtractionException(e1);
			}
			
			try {
				while (reader.hasNext()) {
					final XMLEvent evt;
					
					try {
						evt = reader.nextEvent();
					} catch (XMLStreamException e) {
						throw new MetadataExtractionException(e);
					}
					
					if (evt.isStartElement()) {
						final StartElement element = evt.asStartElement();
						final Attribute attTitle = element.getAttributeByName(new QName("title"));
						final Attribute attShortTitle = element.getAttributeByName(new QName("short-title"));
						
						if (attTitle != null)
							m.setTitle(attTitle.getValue());
						
						if (attShortTitle != null)
							m.setShortTitle(attShortTitle.getValue());
						
						break;
					}
				}
			} finally {
				try {
					reader.close();
				} catch (XMLStreamException e) {
					log.error("Cannot close source reader", e);
				}
			}
		}
		
		if (m.getTitle() == null || m.getTitle().isEmpty()) {
			log.warn("Missing page title of " + uri);
			m.setTitle(source.getName());
		}
		
		if (m.getShortTitle() == null)
			m.setShortTitle(m.getTitle());
		
		return m;
	}
}
