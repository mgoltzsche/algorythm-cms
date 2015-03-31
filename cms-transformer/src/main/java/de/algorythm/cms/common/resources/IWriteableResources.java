package de.algorythm.cms.common.resources;

import java.nio.file.Path;

public interface IWriteableResources extends IOutputTargetFactory {

	Path resolvePublicPath(String publicPath);
}
