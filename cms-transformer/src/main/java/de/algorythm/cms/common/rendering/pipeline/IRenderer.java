package de.algorythm.cms.common.rendering.pipeline;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

public interface IRenderer {

	void renderAll(Format format, IOutputTargetFactory outFactory) throws Exception;
	//byte[] renderPage(String path, OutputFormat format);
}
