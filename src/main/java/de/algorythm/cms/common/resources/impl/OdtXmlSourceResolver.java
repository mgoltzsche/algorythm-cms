package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.impl.OdtMetadataExtractor;

public class OdtXmlSourceResolver extends AbstractXmlSourceResolver {

	static private final IMetadataExtractor metadataExtractor = new OdtMetadataExtractor();
	
	public OdtXmlSourceResolver() {
		super(metadataExtractor);
	}
	
	@Override
	protected Source createXmlSourceInternal(URI uri, IBundleRenderingContext ctx)
			throws IOException, ResourceNotFoundException {
		final Path extractedOdtDirectory = ctx.unzip(uri);
		final Path contentXmlFile = extractedOdtDirectory.resolve("content.xml");
		
		return new XmlSource(uri.getPath() + "/content.xml", contentXmlFile);
	}
}
