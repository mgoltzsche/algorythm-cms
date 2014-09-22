package de.algorythm.cms.common.renderer;

import java.io.File;
import java.io.Writer;

import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;

public interface IContentRenderer {

	void render(ISite site, IPage page, Writer writer) throws RendererException;
	void render(File contentFile, Writer writer) throws RendererException;
}
