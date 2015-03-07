package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public interface IArchiveExtractor {

	Path unzip(URI uri) throws ResourceNotFoundException, IOException;
}
