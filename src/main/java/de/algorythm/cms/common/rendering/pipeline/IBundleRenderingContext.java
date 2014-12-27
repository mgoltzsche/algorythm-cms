package de.algorythm.cms.common.rendering.pipeline;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;

public interface IBundleRenderingContext extends IXmlContext {

	IBundle getBundle();
	IPageConfig getStartPage(Locale locale);
	void setStartPage(Locale locale, IPageConfig page);
	ISourceUriResolver getResourceResolver();
	ITargetUriResolver getOutputResolver();
	IXmlContext getXmlLoader();
	Path getTempDirectory();
	Path getOutputDirectory();
	URI getResourcePrefix();
	String getProperty(String name);
	void setProperty(String name, String value);
}
