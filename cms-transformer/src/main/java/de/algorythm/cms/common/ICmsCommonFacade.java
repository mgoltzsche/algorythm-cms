package de.algorythm.cms.common;

import java.net.URI;
import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IInputResolver;

public interface ICmsCommonFacade {

	IInputResolver createInputResolver(Path repoDirectory);
	IBundle loadBundle(URI publicBundleUri, IInputResolver resolver);
	IRenderer createRenderer(IBundle bundle, IInputResolver resolver);
	void shutdown();
}
