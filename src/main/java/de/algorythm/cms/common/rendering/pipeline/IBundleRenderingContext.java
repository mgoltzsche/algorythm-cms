package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IDestinationPathResolver;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public interface IBundleRenderingContext extends IXmlContext, IArchiveExtractor, ISourcePathResolver, IDestinationPathResolver {

	IBundle getBundle();
	Path getTempDirectory();
	Source createXmlSource(URI uri) throws ResourceNotFoundException, IOException;
	Marshaller createMarshaller() throws JAXBException;
	URI getResourcePrefix();
	String getProperty(String name);
	void setProperty(String name, String value);
}
