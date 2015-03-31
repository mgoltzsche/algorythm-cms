package de.algorythm.cms.common.resources;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.bundle.IBundle;

public interface IBundleExpander {

	IBundle expandedBundle(IBundle bundle, IInputResolver resolver) throws ResourceNotFoundException, IOException, JAXBException;
}
