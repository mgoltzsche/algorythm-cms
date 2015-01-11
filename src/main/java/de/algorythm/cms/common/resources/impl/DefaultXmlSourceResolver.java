package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.impl.CmsMetadataExtractor;

public class DefaultXmlSourceResolver extends AbstractXmlSourceResolver {

	static private final IMetadataExtractor metadataExtractor = new CmsMetadataExtractor();
	
	public DefaultXmlSourceResolver() {
		super(metadataExtractor);
	}

	@Override
	protected Source createXmlSourceInternal(URI uri, IBundleRenderingContext ctx) throws IOException, ResourceNotFoundException {
		return new XmlSource(uri, ctx.resolveSource(uri));
	}
}
