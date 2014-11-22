package de.algorythm.cms.common.model.loader;

import java.io.File;

import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IBundleLoader {

	IBundle getBundle(File bundleFile) throws JAXBException;
}
