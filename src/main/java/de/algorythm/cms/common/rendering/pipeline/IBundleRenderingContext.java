package de.algorythm.cms.common.rendering.pipeline;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;

public interface IBundleRenderingContext {

	IBundle getBundle();
	IPageConfig getStartPage(Locale locale);
	void setStartPage(Locale locale, IPageConfig page);
	ISourceUriResolver getResourceResolver();
	ITargetUriResolver getOutputResolver();
	IXmlLoader getXmlLoader();
	Path getTempDirectory();
	Path getOutputDirectory();
	URI getResourcePrefix();
	String getProperty(String name);
	void setProperty(String name, String value);
	Document getDocument(URI uri, Locale locale);
	Templates compileTemplates(Collection<URI> xslSourceUris);
	Transformer createTransformer(Templates templates, URI notFoundContent, Locale locale) throws TransformerConfigurationException;
	void transform(URI sourceUri, URI targetUri, Transformer transformer, Locale locale) throws IOException, TransformerException;
}
