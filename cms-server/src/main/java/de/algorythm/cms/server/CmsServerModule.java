package de.algorythm.cms.server;

import javax.servlet.Servlet;

import com.google.inject.servlet.ServletModule;

import de.algorythm.cms.server.servlet.CmsPageServlet;

public class CmsServerModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("*.html").with(CmsPageServlet.class);
	}
}
