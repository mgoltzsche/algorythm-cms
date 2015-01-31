package de.algorythm.cms.common.resources;

import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IBundleExpander {

	IBundle expandBundle(IBundle bundle, Path expandDirectory);
}
