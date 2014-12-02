package de.algorythm.cms.common;

import java.io.File;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.scheduling.IFuture;

public interface ICmsCommonFacade {

	IBundle loadBundle(File bundleXml);
	IFuture render(IBundle bundle, File outputDirectory);
	void generatePagesXml(IBundle bundle, File outputDirectory);
	void generateSite(IBundle bundle, File tmpDirectory, File outputDirectory);
}
