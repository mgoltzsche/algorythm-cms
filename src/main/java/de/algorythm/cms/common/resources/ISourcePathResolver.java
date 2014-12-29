package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;

public interface ISourcePathResolver {

	Path resolveSource(URI publicUri) throws ResourceNotFoundException;
}
