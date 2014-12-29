package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;

public interface IDestinationPathResolver {

	Path resolveDestination(URI publicUri);
}
