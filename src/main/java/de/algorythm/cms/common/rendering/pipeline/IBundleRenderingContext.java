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
	Templates compileTemplates(Collection<URI> xslSourceUris);
	Transformer createTransformer(Templates templates, URI notFoundContent) throws TransformerConfigurationException;
	void transform(URI sourceUri, URI targetUri, Transformer transformer) throws IOException, TransformerException;
}
