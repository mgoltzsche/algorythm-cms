package de.algorythm.cms.common;

import java.nio.file.Path;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.scheduling.IFuture;

public interface ICmsCommonFacade {

	IBundle loadBundle(Path bundleXml);
	IFuture<Void> render(IBundle bundle, Path outputDirectory);
}
