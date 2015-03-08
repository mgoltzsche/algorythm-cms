package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IOutputStreamFactory;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public interface IBundleRenderingContext extends IXmlContext, IArchiveExtractor, ISourcePathResolver, IOutputStreamFactory {

	IBundle getBundle();
	Path getTempDirectory();
	Source createXmlSource(URI uri) throws ResourceNotFoundException, IOException;
	Marshaller createMarshaller() throws JAXBException;
	URI getResourcePrefix();
	IMetadata extractMetadata(URI uri) throws ResourceNotFoundException, MetadataExtractionException;
	String getProperty(String name);
	void setProperty(String name, String value);
}
