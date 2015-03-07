package de.algorythm.cms.server.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import de.algorythm.cms.common.CmsCommonModule;
import de.algorythm.cms.server.CmsServerModule;

public class CmsServletContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new CmsServerModule(), new CmsCommonModule());
	}
}