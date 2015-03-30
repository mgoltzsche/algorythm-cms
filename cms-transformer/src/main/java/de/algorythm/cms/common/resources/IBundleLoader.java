package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.net.URI;

import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.bundle.IBundle;

public interface IBundleLoader {

	IBundle loadBundle(URI publicUri, ISourcePathResolver resolver) throws ResourceNotFoundException, IOException, JAXBException;
}
