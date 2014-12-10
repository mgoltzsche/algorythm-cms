package de.algorythm.cms.common.model.loader;

import java.nio.file.Path;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;

public interface IBundleLoader {

	IBundle getBundle(Path bundleFile) throws JAXBException;
	IPage loadPages(IBundle bundle, Locale locale);
}
