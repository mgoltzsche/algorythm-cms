package de.algorythm.cms.common.model.entity;

import java.util.Locale;

public interface ISite extends Comparable<ISite> {

	String getName();
	String getTitle();
	String getDescription();
	String getContextPath();
	Locale getDefaultLocale();
	String getDefaultTemplate();
	IPage getStartPage();
}
