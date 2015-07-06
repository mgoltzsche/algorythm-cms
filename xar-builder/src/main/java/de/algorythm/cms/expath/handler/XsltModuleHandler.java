package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.XsltComponent;

import java.io.IOException;
import java.net.URI;

/**
 * Created by max on 31.05.15.
 */
public class XsltModuleHandler extends AbstractComponentHandler {

    @Override
    public void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath) throws IOException {
        final URI importUri = deriveImportUriFromName(pkg, packageRelativeFilePath);

        pkg.getComponents().add(new XsltComponent(packageRelativeFilePath, importUri));
    }
}
