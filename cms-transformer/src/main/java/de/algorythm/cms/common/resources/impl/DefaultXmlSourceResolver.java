package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
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
		return new XmlSource(uri, ctx.createInputStream(uri));
	}
}
