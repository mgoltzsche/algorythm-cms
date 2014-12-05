package de.algorythm.cms.common;

import java.io.File;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.scheduling.IFuture;

public interface ICmsCommonFacade {

	IBundle loadBundle(File bundleXml);
	IFuture<Void> render(IBundle bundle, File outputDirectory);
}
