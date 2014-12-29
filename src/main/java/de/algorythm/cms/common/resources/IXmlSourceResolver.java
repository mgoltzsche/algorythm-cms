package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;

public interface IXmlSourceResolver {

	Source createXmlSource(URI uri, IBundleRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
