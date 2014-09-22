package de.algorythm.cms.common;

import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.Module;

import de.algorythm.cms.common.model.entity.ISite;

public class CmsCommonMain {

	static public void main(String[] args) {
		final Module module = new CmsCommonModule();
		final CmsCommonMain main = new CmsCommonMain(module);
		
		main.generateAll();
	}
	

	@Inject
	private ICmsCommonFacade facade;
	
	public CmsCommonMain(final Module module) {
		Guice.createInjector(module).injectMembers(this);
	}
	
	public void generateAll() {
		for (ISite site : facade.listSites()) {
			facade.generatePagesXml(site);
			facade.generateSite(site);
		}
	}
}
