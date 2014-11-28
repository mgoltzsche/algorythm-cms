package de.algorythm.cms.common.rendering.pipeline;

import java.io.File;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IResourceResolver;

public interface IBundleRenderingContext {

	IBundle getBundle();
	IResourceResolver getResourceResolver();
	File getTempDirectory();
	File getOutputDirectory();
	String getProperty(String name);
	void setProperty(String name, String value);
}
