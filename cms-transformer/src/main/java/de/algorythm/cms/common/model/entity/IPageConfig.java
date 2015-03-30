package de.algorythm.cms.common.model.entity;

import java.net.URI;
import java.util.List;

public interface IPageConfig {

	String getName();
	URI getSource();
	String getTitle();
	String getNavigationTitle();
	List<IPageConfig> getPages();
	boolean isInNavigation();
}