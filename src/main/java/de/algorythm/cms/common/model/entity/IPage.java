package de.algorythm.cms.common.model.entity;

public interface IPage extends IPageContainer {

	String getTitle();
	String getNavigationTitle();
	boolean isInNavigation();
}
