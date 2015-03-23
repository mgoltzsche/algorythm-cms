package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Source;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;

public interface IXmlSourceResolver {

	Source createXmlSource(URI uri, IRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
