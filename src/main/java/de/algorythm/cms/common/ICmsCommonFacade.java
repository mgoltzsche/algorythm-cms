package de.algorythm.cms.common;

import java.util.List;

import de.algorythm.cms.common.model.entity.ISite;

public interface ICmsCommonFacade {

	List<ISite> listSites();
	void generatePagesXml(ISite site);
	void generateSite(ISite site);
}
