package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

@Singleton
public class OdtXmlSourceResolver extends AbstractXmlSourceResolver {

	private final IArchiveExtractor archiveExtractor;

	@Inject
	public OdtXmlSourceResolver(IXmlFactory xmlFactory, IMetadataExtractorProvider metadataExtractorProvider, IArchiveExtractor archiveExtractor) {
		super(xmlFactory, metadataExtractorProvider);
		this.archiveExtractor = archiveExtractor;
	}

	@Override
	protected Source createXmlSourceInternal(final URI uri, final IRenderingContext ctx)
			throws IOException, ResourceNotFoundException {
		final Path extractedOdtDirectory = archiveExtractor.unzip(uri, ctx, ctx.getTmpResources());
		final Path contentXmlFile = extractedOdtDirectory.resolve("content.xml");
		
		return new XmlSource(uri.getPath() + "/content.xml", contentXmlFile);
	}
}
