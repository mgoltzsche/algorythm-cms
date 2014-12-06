package de.algorythm.cms.common.rendering.pipeline;

import java.io.File;
import java.net.URI;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IResourceResolver;

public interface IBundleRenderingContext {

	IBundle getBundle();
	IResourceResolver getInputUriResolver();
	IOutputUriResolver getOutputUriResolver();
	URI getPublicResourceOutputDirectory();
	File getTempDirectory();
	File getOutputDirectory();
	String getProperty(String name);
	void setProperty(String name, String value);
}
