package de.algorythm.cms.common.resources;

import java.nio.file.Path;

import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IBundleLoader {

	IBundle getBundle(Path bundleFile) throws JAXBException;
}
