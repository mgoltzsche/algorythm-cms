package de.algorythm.cms.common.rendering.pipeline;

import java.io.File;

import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.resources.IResourceResolver;

public interface IRenderer {

	void render(IResourceResolver resourceResolver, File tmpDirectory, File outputDirectory, Iterable<IOutputConfiguration> outputCfgs);
}
