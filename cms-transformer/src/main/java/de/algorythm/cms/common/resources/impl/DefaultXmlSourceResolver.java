package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

@Singleton
public class DefaultXmlSourceResolver extends AbstractXmlSourceResolver {

	@Inject
	public DefaultXmlSourceResolver(final IXmlFactory xmlFactory, final IMetadataExtractorProvider metadataExtractor) {
		super(xmlFactory, metadataExtractor);
	}

	@Override
	protected Source createXmlSourceInternal(URI uri, IRenderingContext ctx) throws IOException, ResourceNotFoundException {
		final InputStream stream = ctx.createInputStream(uri);
		
		if (stream == null)
			throw new ResourceNotFoundException("Cannot find XML source at " + uri);
		
		return new XmlSource(uri, stream);
	}
}
