package de.algorythm.cms.common;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IInputResolver;

public interface IRendererFactory {

	IRenderer createRenderer(IBundle bundle, IInputResolver resolver);
}
