package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.Resource;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Derives an expath resource component description.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public class ResourceHandler extends AbstractComponentHandler {

    @Override
    public void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath, Charset encoding) throws IOException {
        final URI importUri = deriveImportUriFromName(pkg, packageRelativeFilePath);

        pkg.getComponents().add(new Resource(packageRelativeFilePath, importUri));
    }
}
