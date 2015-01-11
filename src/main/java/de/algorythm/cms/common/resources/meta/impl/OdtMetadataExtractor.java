package de.algorythm.cms.common.resources.meta.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
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

public class OdtMetadataExtractor implements IMetadataExtractor {

	static private Logger log = LoggerFactory.getLogger(CmsMetadataExtractor.class);

	@Override
	public IMetadata extractMetadata(final URI uri, final IBundleRenderingContext ctx)
			throws ResourceNotFoundException, MetadataExtractionException {
		try {
			final Path extractedOdtDirectory = ctx.unzip(uri);
			final Path metaXmlFile = extractedOdtDirectory.resolve("meta.xml");
			final Metadata md = new Metadata(ctx.resolveSource(uri));
			
			if (Files.exists(metaXmlFile)) {
				extractMetadata(metaXmlFile, ctx, md);
			} else {
				log.warn("Missing meta.xml in ODT " + uri.getPath());
			}
			
			return md;
		} catch (IOException | XMLStreamException e) {
			throw new MetadataExtractionException("Cannot extract ODT metadata from " + uri.getPath(), e);
		}
	}
	
	private void extractMetadata(final Path metaXmlFile, final IBundleRenderingContext ctx, final Metadata r) throws IOException, XMLStreamException {
		try (InputStream stream = Files.newInputStream(metaXmlFile)) {
			final XMLEventReader reader = ctx.createXMLEventReader(stream);
			
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