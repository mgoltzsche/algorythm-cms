package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IResourceResolver;

public class RenderingContext implements IBundleRenderingContext {

	private final String resourcePrefix;
	private final IBundle bundle;
	private final IResourceResolver inputUriResolver;
	private final IOutputUriResolver outputUriResolver;
	private final File tempDirectory, outputDirectory;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());

	public RenderingContext(final IResourceResolver inputUriResolver, final IOutputUriResolver outputUriResolver, final String resourcePrefix, final File tempDirectory, final File outputDirectory) {
		this.bundle = inputUriResolver.getMergedBundle();
		this.inputUriResolver = inputUriResolver;
		this.outputUriResolver = outputUriResolver;
		this.tempDirectory = tempDirectory;
		this.outputDirectory = outputDirectory;
		this.resourcePrefix = resourcePrefix;
	}

	@Override
	public IResourceResolver getInputUriResolver() {
		return inputUriResolver;
	}

	@Override
	public IOutputUriResolver getOutputUriResolver() {
		return outputUriResolver;
	}

	@Override
	public String getResourcePrefix() {
		return resourcePrefix;
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public File getTempDirectory() {
		return tempDirectory;
	}

	@Override
	public File getOutputDirectory() {
		return outputDirectory;
	}
}
