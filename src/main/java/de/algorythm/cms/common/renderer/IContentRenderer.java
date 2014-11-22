package de.algorythm.cms.common.renderer;

import java.io.File;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IResourceResolver;

public interface IContentRenderer {

	void render(IBundle bundle, IResourceResolver uriResolver, File outputDirectory) throws RendererException;
}
