package de.algorythm.cms.common.model.loader.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.impl.xml.contentHandler.PageInfoHandler;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.loader.IBundleLoader;

@Singleton
public class BundleLoader implements IBundleLoader {
	
	private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private final JAXBContext jaxbContext;
	
	@Inject
	public BundleLoader(final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
		parserFactory.setNamespaceAware(true);
	}
	
	@Override
	public IBundle getBundle(final File bundleFile) throws JAXBException {
		if (!bundleFile.exists())
			throw new IllegalArgumentException(bundleFile + " does not exist");
		
		if (!bundleFile.isFile())
			throw new IllegalArgumentException(bundleFile + " is a directory");
		
		final Bundle bundle = readBundle(bundleFile);
		
		bundle.setLocation(bundleFile.getParentFile().toURI());
		
		if (bundle.getTitle() == null)
			bundle.setTitle(bundle.getName());
		
		if (bundle.getDefaultLocale() == null)
			bundle.setDefaultLocale(Locale.ENGLISH);
		
		if (bundle.getContextPath() == null)
			bundle.setContextPath("");
		
		return bundle;
	}
	
	private Bundle readBundle(final File siteCfgFile) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Source source = new StreamSource(siteCfgFile.getAbsolutePath());
		
		return unmarshaller.unmarshal(source, Bundle.class).getValue();
	}
	
	@Override
	public IPage loadPages(final IBundle bundle) {
		final IPage startPage;
		
		try {
			final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			final PageInfoHandler pageInfoHandler = new PageInfoHandler();
			final URI rootUri = URI.create(bundle.getLocation() + "/pages");
			
			reader.setContentHandler(pageInfoHandler);
			startPage = loadPages(rootUri, "", bundle.getName(), reader, pageInfoHandler);
		} catch(Exception e) {
			throw new RuntimeException("Cannot load start page of " + bundle.getName(), e);
		}
		
		return startPage;
	}
	
	private IPage loadPages(final URI rootUri, final String path, final String name, final XMLReader reader, final PageInfoHandler handler) throws IOException, SAXException {
		final URI pageUri = URI.create(rootUri + path);
		final PageInfo page = loadPage(rootUri, path, name, reader, handler);
		final List<IPage> subPages = new LinkedList<IPage>();
		
		page.setPages(subPages);
		
		for (File subDir : new File(pageUri).listFiles()) {
			if (subDir.isDirectory()) {
				final String subDirName = subDir.getName();
				final String subPath = path + '/' + subDirName;
				
				subPages.add(loadPages(rootUri, subPath, subDirName, reader, handler));
			}
		}
		
		return page;
	}
	
	private PageInfo loadPage(final URI rootUri, final String path, final String name, final XMLReader reader, final PageInfoHandler handler) throws IOException, SAXException {
		final PageInfo page = new PageInfo(path, name);
		final URI pageUri = URI.create(rootUri + path + "/page.xml");
		final File pageFile = new File(pageUri);
		
		if (pageFile.exists()) {
			handler.setPage(page);
			
			try {
				reader.parse(pageFile.getAbsolutePath());
			} catch (InformationCompleteException e) {
			}
		}
		
		return page;
	}
}
