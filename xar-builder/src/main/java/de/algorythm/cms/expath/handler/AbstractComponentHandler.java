package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.IResourceHandler;
import de.algorythm.cms.expath.model.ExpathPackage;

import java.net.URI;

/**
 * Abstract expath package component handler to derive a component description from a given file.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public abstract class AbstractComponentHandler implements IResourceHandler {

	/**
	 * Derives the namespace or import URI of an expath package component.
	 * @param pkg expath package descriptor
	 * @param pkgRelativeFilePath package relative file path
	 * @return derived namespace for the component referred by {@code pkgRelativeFilePath}
	 */
    protected URI deriveImportUriFromName(ExpathPackage pkg, URI pkgRelativeFilePath) {
        String pkgName = pkg.getName().toString();

        if (pkgName.charAt(pkgName.length() - 1) != '/')
            pkgName += '/';

        return URI.create(pkgName + pkgRelativeFilePath.getPath());
    }
}
