package de.algorythm.cms.common.renderer;

import java.io.File;

import de.algorythm.cms.common.model.entity.ISite;

public interface IContentRenderer {

	void render(ISite site, File outputDirectory) throws RendererException;
}
