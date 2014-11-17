package de.algorythm.cms.common;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Module;

import de.algorythm.cms.common.model.entity.ISite;

public class CmsCommonMain {

	static private final Logger log = LoggerFactory.getLogger(CmsCommonMain.class);
	
	static public void main(String[] args) {
		try {
			final Module module = new CmsCommonModule();
			final CmsCommonMain main = new CmsCommonMain(module);
			
			main.generateAll();
		} catch(Throwable e) {
			log.error("XML transformation failed", e);
			System.exit(1);
		}
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
