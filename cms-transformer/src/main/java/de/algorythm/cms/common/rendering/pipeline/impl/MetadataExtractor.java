package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class MetadataExtractor implements IMetadataExtractor {

	private final Map<String, IMetadataExtractor> extensionMap;

	public MetadataExtractor(final Map<String, IMetadataExtractor> extractors) {
		this.extensionMap = extractors;
	}

	@Override
	public IMetadata extractMetadata(URI uri, IInputResolver resolver, IWriteableResources tmp)
			throws ResourceNotFoundException, MetadataExtractionException, IOException {
		final String extension = FilenameUtils.getExtension(uri.getPath());
		
		if (extension.isEmpty())
			throw new IllegalArgumentException("Cannot infer metadata extractor from URI without handler: " + uri);
		
		final IMetadataExtractor mdExtractor = extensionMap.get(extension);
		
		if (mdExtractor == null)
			throw new UnsupportedOperationException("Unsupported metadata extraction handler: " + extension);
		
		return mdExtractor.extractMetadata(uri, resolver, tmp);
	}
}
