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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.impl.xml.contentHandler.PageInfoHandler;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.entity.impl.ProxyFactory;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.resources.IProxyResolver;

@Singleton
public class BundleLoader implements IBundleLoader {

	private final IProxyResolver<Bundle, IPage> startPageProxyResolver = new IProxyResolver<Bundle, IPage>() {
		@Override
		public IPage resolveProxy(final Bundle context) {
			final IPage startPage;
			
			try {
				final XMLReader reader = saxReaderFactory.createReader();
				final PageInfoHandler pageInfoHandler = new PageInfoHandler();
				final URI rootUri = URI.create(context.getLocation() + "/pages");
				
				reader.setContentHandler(pageInfoHandler);
				startPage = loadPages(rootUri, "", context.getName(), reader, pageInfoHandler);
			} catch(Exception e) {
				throw new RuntimeException("Cannot load start page of " + context.getName(), e);
			}
			
			context.setStartPage(startPage);
			
			return startPage;
		}
	};
	
	private final IXmlReaderFactory saxReaderFactory;
	private final JAXBContext jaxbContext;
	
	@Inject
	public BundleLoader(final Configuration cfg, final IXmlReaderFactory readerFactory) throws JAXBException {
		this.saxReaderFactory = readerFactory;
		this.jaxbContext = JAXBContext.newInstance(Bundle.class);
	}
	
	@Override
	public IBundle getBundle(final File bundleFile) throws JAXBException {
		if (!bundleFile.exists())
			throw new IllegalArgumentException(bundleFile + " does not exist");
		
		if (!bundleFile.isFile())
			throw new IllegalArgumentException(bundleFile + " is a directory");
		
		final Bundle bundle = readSiteConfig(bundleFile);
		
		bundle.setLocation(bundleFile.getParentFile().toURI());
		
		if (bundle.getTitle() == null)
			bundle.setTitle(bundle.getName());
		
		if (bundle.getDefaultLocale() == null)
			bundle.setDefaultLocale(Locale.ENGLISH);
		
		if (bundle.getContextPath() == null)
			bundle.setContextPath("");
		
		bundle.setStartPage(new ProxyFactory().createProxy(startPageProxyResolver, bundle, IPage.class));
		
		return bundle;
	}
	
	private Bundle readSiteConfig(final File siteCfgFile) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Source source = new StreamSource(siteCfgFile.getAbsolutePath());
		
		return unmarshaller.unmarshal(source, Bundle.class).getValue();
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
