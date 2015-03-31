package de.algorythm.cms.common.rendering.pipeline;

import java.net.URI;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;

public interface IRenderingContext extends IInputResolver {

	String getName();
	URI getResourcePrefix();
	IWriteableResources getMetaResources();
	IWriteableResources getTmpResources();
}
