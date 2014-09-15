package de.algorythm.cms.common.model.entity;

import java.util.Locale;

public interface ISite extends IPageContainer {

	String getTitle();
	String getContextPath();
	Locale getDefaultLocale();
}
