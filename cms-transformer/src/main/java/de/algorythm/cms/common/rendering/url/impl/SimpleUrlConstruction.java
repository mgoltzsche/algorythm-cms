package de.algorythm.cms.common.rendering.url.impl;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.url.IUrlConstruction;
import de.algorythm.cms.common.rendering.url.LocalizedPath;

public class SimpleUrlConstruction implements IUrlConstruction {

	@Override
	public LocalizedPath fromUrl(String absoluteUrl, IBundle bundle) {
		return new LocalizedPath(absoluteUrl, bundle.getDefaultLocale());
	}

	@Override
	public String toUrl(LocalizedPath path, IBundle bundle) {
		return path.getPath();
	}
}
