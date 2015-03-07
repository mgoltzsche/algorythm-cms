package de.algorythm.cms.common;

import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;

public interface ICmsCommonFacade {

	IBundle loadBundle(Path bundleXml);
	IRenderer createRenderer(IBundle bundle);
	void shutdown();
}
