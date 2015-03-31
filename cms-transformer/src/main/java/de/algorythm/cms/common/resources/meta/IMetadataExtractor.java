package de.algorythm.cms.common.resources.meta;

import java.io.IOException;
import java.net.URI;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public interface IMetadataExtractor {

	IMetadata extractMetadata(URI uri, IInputResolver resolver, IWriteableResources tmp) throws ResourceNotFoundException, MetadataExtractionException, IOException;
}