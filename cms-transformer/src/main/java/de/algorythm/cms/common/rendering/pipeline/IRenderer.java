package de.algorythm.cms.common.rendering.pipeline;

import java.net.URI;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public interface IRenderer {

	void renderAll(Format format, IOutputTargetFactory outFactory) throws Exception;
	void expand() throws Exception;
	void renderStaticResources(Format format, IOutputTargetFactory outFactory) throws Exception;
	byte[] renderArtifact(URI outputUri) throws Exception, ResourceNotFoundException;
}
