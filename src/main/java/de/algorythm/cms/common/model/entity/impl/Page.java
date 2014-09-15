package de.algorythm.cms.common.model.entity.impl;

import java.util.List;

import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.IPage;

public class Page extends AbstractPageContainer implements IPage {

	private String title;
	private String navigationTitle;
	private boolean inNavigation;
	private List<IPage> pages;

	public Page(final XmlResourceDao dao, final String parentPath, final String name) {
		super(dao, parentPath, name);
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getNavigationTitle() {
		return navigationTitle;
	}

	@Override
	public boolean isInNavigation() {
		return inNavigation;
	}

	@Override
	public List<IPage> getPages() {
		return pages;
	}
	
	@Override
	public String toString() {
		return "Page [" + getPath() + "]";
	}
}