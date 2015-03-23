package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;

public interface IArchiveExtractor {

	Path unzip(URI uri, IRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
