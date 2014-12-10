package de.algorythm.cms.common.rendering.pipeline;

import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.scheduling.IFuture;

public interface IRenderer {

	IFuture<Void> render(IBundle bundle, Path tmpDirectory, Path outputDirectory);
}
