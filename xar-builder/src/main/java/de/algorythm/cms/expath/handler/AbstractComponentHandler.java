package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.IResourceHandler;
import de.algorythm.cms.expath.model.ExpathPackage;

import java.net.URI;

/**
 * Created by max on 05.06.15.
 */
public abstract class AbstractComponentHandler implements IResourceHandler {

    protected URI deriveImportUriFromName(ExpathPackage pkg, URI packageRelativeFilePath) {
        String pkgName = pkg.getName().toString();

        if (pkgName.charAt(pkgName.length() - 1) != '/')
            pkgName += '/';

        return URI.create(pkgName + packageRelativeFilePath.getPath());
    }
}
