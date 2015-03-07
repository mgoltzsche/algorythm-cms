package de.algorythm.cms.common.resources.meta.impl;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.impl.Metadata;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class CmsMetadataExtractor implements IMetadataExtractor {

	static private Logger log = LoggerFactory.getLogger(CmsMetadataExtractor.class);

	@Override
	public IMetadata extractMetadata(final URI uri, final IBundleRenderingContext ctx) throws ResourceNotFoundException, MetadataExtractionException {
		final Path xmlFile = ctx.resolveSource(uri);
		
		try {
			final Metadata m = new Metadata(xmlFile);
			
			try (InputStream stream = Files.newInputStream(xmlFile)) {
				final XMLEventReader reader = ctx.createXMLEventReader(stream);
				
				try {
					while (reader.hasNext()) {
						final XMLEvent evt = reader.nextEvent();
						
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
					reader.close();
				}
			}
			
			if (m.getTitle() == null || m.getTitle().isEmpty()) {
				log.warn("Missing page title of " + xmlFile);
				m.setTitle(xmlFile.getFileName().toString());
			}
			
			if (m.getShortTitle() == null)
				m.setShortTitle(m.getTitle());
			
			return m;
		} catch(Exception e) {
			throw new MetadataExtractionException("Cannot extract metadata from " + xmlFile, e);
		}
	}
}
