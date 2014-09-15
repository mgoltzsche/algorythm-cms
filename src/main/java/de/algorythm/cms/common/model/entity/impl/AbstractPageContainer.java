package de.algorythm.cms.common.model.entity.impl;

import java.util.List;

import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IPageContainer;

public class AbstractPageContainer implements IPageContainer {

	private final String name;
	private final String path;
	protected List<IPage> pages;
	private final XmlResourceDao dao;
	
	public AbstractPageContainer(final XmlResourceDao dao, final String parentPath, final String name) {
		this.name = name;
		this.dao = dao;
		this.path = parentPath + '/' + name;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<IPage> getPages() {
		if (pages == null)
			pages = dao.loadPages(path);
		
		return pages;
	}
	
	@Override
	public int compareTo(final IPageContainer o) {
		return o.getName().compareTo(name);
	}
}
