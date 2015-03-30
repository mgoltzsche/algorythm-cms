package de.algorythm.cms.common.rendering.pipeline;

import java.net.URI;

import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ISourcePathResolver;

public interface IRenderingContext extends ISourcePathResolver, IOutputTargetFactory {

	URI getResourcePrefix();
}
