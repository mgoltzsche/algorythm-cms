package de.algorythm.cms.common.model.entity.bundle;

import java.net.URI;
import java.util.List;

public interface IPage {

	String getName();
	URI getSource();
	String getTitle();
	String getNavigationTitle();
	List<IPage> getPages();
	boolean isInNavigation();
}
