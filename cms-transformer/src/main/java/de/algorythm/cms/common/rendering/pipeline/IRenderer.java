package de.algorythm.cms.common.rendering.pipeline;

import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.impl.Page;
import de.algorythm.cms.common.scheduling.IFuture;

public interface IRenderer {

	IFuture<Void> render(Path outputDirectory);
	//byte[] renderPage(Page page);
}
