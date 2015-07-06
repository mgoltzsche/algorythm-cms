package de.algorythm.cms.expath;

import de.algorythm.cms.expath.model.ExpathPackage;

import java.io.IOException;
import java.net.URI;

/**
 * Created by max on 31.05.15.
 */
public interface IResourceHandler {

    void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath) throws IOException;
}
