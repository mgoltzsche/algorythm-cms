package de.algorythm.cms.expath;

import de.algorythm.cms.expath.model.ExpathPackage;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Resource handler interface.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public interface IResourceHandler {

	/**
	 * Derives the component description and adds it to the {@code package}.
	 * @param pkg expath package descriptor
	 * @param localFile local file path
	 * @param packageRelativeFilePath package relative file path
	 * @param encoding component file encoding
	 * @throws IOException if file could not be accessed
	 */
    void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath, Charset encoding) throws IOException;
}
