package de.algorythm.cms.common.rendering.pipeline;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;

public interface IBundleRenderingContext {

	IBundleRenderingContext createLocalized(Locale locale, boolean localizeOutput);
	IBundle getBundle();
	IUriResolver getResourceResolver();
	IOutputUriResolver getOutputResolver();
	IXmlLoader getXmlLoader();
	Path getTempDirectory();
	Path getOutputDirectory();
	URI getResourcePrefix();
	String getProperty(String name);
	void setProperty(String name, String value);
	XMLReader createXmlReader();
	Document getDocument(URI uri);
	Document transform(Document document, Transformer transformer);
}
