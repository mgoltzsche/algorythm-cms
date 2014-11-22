package de.algorythm.cms.common.resources;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IDependencyLoader {

	IBundle loadDependency(String dependencyName);
}
