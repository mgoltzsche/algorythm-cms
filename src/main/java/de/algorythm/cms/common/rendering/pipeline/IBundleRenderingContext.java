package de.algorythm.cms.common.rendering.pipeline;

import java.nio.file.Path;

import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;

public interface IBundleRenderingContext {

	IBundle getBundle();
	IUriResolver getResourceResolver();
	IOutputUriResolver getOutputResolver();
	Path getTempDirectory();
	Path getOutputDirectory();
	Path getResourcePrefix();
	String getProperty(String name);
	void setProperty(String name, String value);
	XMLReader createXmlReader();
}
