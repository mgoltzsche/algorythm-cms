package de.algorythm.cms.common;

import java.io.File;

import de.algorythm.cms.common.model.entity.IBundle;

public interface ICmsCommonFacade {

	IBundle loadBundle(File bundleXml);
	void render(IBundle bundle, File outputDirectory);
	void generatePagesXml(IBundle bundle, File outputDirectory);
	void generateSite(IBundle bundle, File tmpDirectory, File outputDirectory);
}
