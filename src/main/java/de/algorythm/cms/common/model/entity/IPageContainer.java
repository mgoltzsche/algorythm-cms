package de.algorythm.cms.common.model.entity;

import java.util.List;

public interface IPageContainer extends Comparable<IPageContainer> {

	String getName();
	List<IPage> getPages();
}
