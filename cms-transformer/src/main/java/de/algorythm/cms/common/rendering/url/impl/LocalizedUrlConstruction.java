package de.algorythm.cms.common.rendering.url.impl;

import java.util.Locale;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.url.IUrlConstruction;
import de.algorythm.cms.common.rendering.url.LocalizedPath;
import de.algorythm.cms.common.rendering.url.UnsupportedLocaleException;

public class LocalizedUrlConstruction implements IUrlConstruction {

	@Override
	public LocalizedPath fromUrl(final String absoluteUrl, final IBundle bundle) throws UnsupportedLocaleException {
		String path = absoluteUrl;
		Locale locale;
		
		if (bundle.getSupportedLocales().size() == 1) {
			locale = bundle.getDefaultLocale();
		} else {
			final int nextSlashPos = path.indexOf('/', 1);
			
			if (nextSlashPos == -1) {
				locale = bundle.getDefaultLocale();
			} else {
				final String langTag = path.substring(1, nextSlashPos);
				locale = Locale.forLanguageTag(langTag);
				
				if (locale == null)
					locale = bundle.getDefaultLocale();
				else if (!bundle.getSupportedLocales().contains(locale))
					throw new UnsupportedLocaleException(langTag);
				
				path = path.substring(nextSlashPos);
			}
		}
		
		return new LocalizedPath(absoluteUrl, locale);
	}

	@Override
	public String toUrl(LocalizedPath path, IBundle bundle) {
		return path.getPath();
	}
}
