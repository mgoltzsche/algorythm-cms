package de.algorythm.cms.common.renderer;

import java.io.File;

public interface IContentRenderer {

	String render(File contentFile) throws RendererException;
}
