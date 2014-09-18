package de.algorythm.cms.common.model.entity.impl;

import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.IPage;

public class Page extends AbstractPageContainer implements IPage {

	private final String title;
	private final String navigationTitle;
	private final boolean inNavigation;

	public Page(final XmlResourceDao dao, final String site, final String parentPath, final String name, final String title, String navTitle, final boolean inNavigation) {
		super(dao, site, parentPath + '/' + name, name);
		this.title = title;
		this.navigationTitle = navTitle;
		this.inNavigation = inNavigation;
	}

	@Override
	public String getTitle() {
		return title;
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
	public String toString() {
		return "Page [" + getPath() + "]";
	}
}