package de.algorythm.cms.common.resources.meta;

import java.net.URI;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public interface IMetadataExtractor {

	IMetadata extractMetadata(URI uri, IRenderingContext ctx) throws ResourceNotFoundException, MetadataExtractionException;
}