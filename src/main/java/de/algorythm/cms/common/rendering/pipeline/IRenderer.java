package de.algorythm.cms.common.rendering.pipeline;

import java.io.File;

import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.scheduling.IFuture;

public interface IRenderer {

	IFuture<Void> render(IResourceResolver resourceResolver, File tmpDirectory, File outputDirectory, Iterable<IOutputConfig> outputCfgs);
}
