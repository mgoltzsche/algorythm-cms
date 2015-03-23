package de.algorythm.cms.common.rendering.pipeline.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.impl.CmsMetadataExtractor;
import de.algorythm.cms.common.resources.meta.impl.OdtMetadataExtractor;

@Singleton
public class MetadataExtractorFactory implements IMetadataExtractorProvider {

	private final IMetadataExtractor metadataExtractor;

	@Inject
	public MetadataExtractorFactory(final CmsMetadataExtractor cmsMetadataExtractor, OdtMetadataExtractor odtMetadataExtractor) {
		final Map<String, IMetadataExtractor> extensionMap = new HashMap<String, IMetadataExtractor>();
		
		extensionMap.put("xml", cmsMetadataExtractor);
		extensionMap.put("odt", odtMetadataExtractor);
		
		metadataExtractor = new MetadataExtractor(extensionMap);
	}

	@Override
	public IMetadataExtractor getMetadataExtractor() {
		return metadataExtractor;
	}

}
