package de.algorythm.cms.common.model.entity.impl;

import java.util.List;

import org.xml.sax.SAXException;

import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IPageContainer;

public class AbstractPageContainer implements IPageContainer {

	private final String site;
	private final String name;
	private final String path;
	protected List<IPage> pages;
	private final XmlResourceDao dao;
	
	public AbstractPageContainer(final XmlResourceDao dao, final String site, final String path, final String name) {
		this.site = site;
		this.name = name;
		this.dao = dao;
		this.path = path;
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
		if (pages == null) {
			try {
				pages = dao.loadPages(site, path);
			} catch(SAXException e) {
				throw new IllegalStateException("Cannot load pages of " + path, e);
			}
		}
		
		return pages;
	}
	
	@Override
	public int compareTo(final IPageContainer o) {
		return name.compareTo(o.getName());
	}
}
