package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.XsltComponent;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Derives an expath xslt component description.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public class XsltModuleHandler extends AbstractComponentHandler {

    @Override
    public void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath, Charset encoding) throws IOException {
        final URI importUri = deriveImportUriFromName(pkg, packageRelativeFilePath);

        pkg.getComponents().add(new XsltComponent(packageRelativeFilePath, importUri));
    }
}
