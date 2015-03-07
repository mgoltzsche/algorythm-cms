package de.algorythm.cms.common;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;

public interface IRendererFactory {

	IRenderer createRenderer(IBundle bundle);
}
