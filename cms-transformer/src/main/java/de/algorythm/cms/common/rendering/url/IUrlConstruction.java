package de.algorythm.cms.common.rendering.url;

import de.algorythm.cms.common.model.entity.bundle.IBundle;

public interface IUrlConstruction {

	LocalizedPath fromUrl(String url, IBundle bundle) throws UnsupportedLocaleException;
	String toUrl(LocalizedPath path, IBundle bundle);
}
