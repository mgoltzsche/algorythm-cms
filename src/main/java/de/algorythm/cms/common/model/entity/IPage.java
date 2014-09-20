package de.algorythm.cms.common.model.entity;

import java.util.List;

public interface IPage {

	String getName();
	String getPath();
	String getTitle();
	String getNavigationTitle();
	List<IPage> getPages();
	boolean isInNavigation();
}
