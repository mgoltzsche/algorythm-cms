package de.algorythm.cms.common.model.index.impl;

import java.io.File;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.LocaleResolver;
import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.impl.xml.contentHandler.IncludingHandler;
import de.algorythm.cms.common.impl.xml.contentHandler.PageInfoHandler;
import de.algorythm.cms.common.impl.xml.contentHandler.SiteInfoHandler;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.entity.impl.SiteInfo;
import de.algorythm.cms.common.model.index.ISiteIndex;
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.resources.IResourceUriResolver;
import de.algorythm.cms.common.resources.impl.ContentUriResolver;
import de.algorythm.cms.common.util.FilePathUtil;

@Singleton
public class XmlSiteIndex implements ISiteIndex {

	static private final Logger log = LoggerFactory.getLogger(XmlSiteIndex.class);
	
	private final Locale defaultLocale;
	private final File repositoryDirectory;
	private final IXmlReaderFactory saxReaderFactory;
	private final LocaleResolver locales;
	private final IResourceUriResolver contentUriResolver;
	private final JAXBContext jaxbContext;
	
	@Inject
	public XmlSiteIndex(final Configuration cfg, final LocaleResolver locales, final IXmlReaderFactory readerFactory) throws JAXBException {
		repositoryDirectory = cfg.repository;
		defaultLocale = cfg.defaultLanguage;
		this.locales = locales;
		this.saxReaderFactory = readerFactory;
		this.contentUriResolver = new ContentUriResolver(cfg);
		this.jaxbContext = JAXBContext.newInstance(SiteInfo.class);
	}
	
	public List<ISite> getSites() throws SAXException, IOException, JAXBException {
		final File[] rootFiles = repositoryDirectory.listFiles();
		final ArrayList<ISite> sites = new ArrayList<ISite>(rootFiles.length);
		final PageInfoHandler pageInfoHandler = new PageInfoHandler();
		final XMLReader reader = saxReaderFactory.createReader();
		
		reader.setContentHandler(pageInfoHandler);
		reader.setErrorHandler(pageInfoHandler);
		
		for (File siteDir : rootFiles) {
			if (!siteDir.isDirectory())
				continue;
			
			final File siteConfigFile = new File(siteDir, "site-config.xml");
			
			if (!siteConfigFile.exists())
				continue;
			
			final String siteName = siteDir.getName();
			final SiteInfo site = readSiteConfig(siteConfigFile);
			final File rootDirectory = new File(new File(repositoryDirectory, siteName), "pages");
			
			site.setName(siteName);
			
			if (site.getTitle() == null)
				site.setTitle(siteName);
			
			if (site.getDefaultLocale() == null)
				site.setDefaultLocale(Locale.ENGLISH);
			
			if (site.getContextPath() == null)
				site.setContextPath("");
			
			site.setStartPage(loadPages(rootDirectory, "", siteName, reader, pageInfoHandler));
			sites.add(site);
		}
		
		sites.trimToSize();
		Collections.sort(sites);
		
		return sites;
	}
	
	private SiteInfo readSiteConfig(final File siteCfgFile) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Source source = new StreamSource(siteCfgFile.getAbsolutePath());
		
		return unmarshaller.unmarshal(source, SiteInfo.class).getValue();
	}
	
	private IPage loadPages(final File rootDirectory, final String path, final String name, final XMLReader reader, final PageInfoHandler handler) throws IOException, SAXException {
		final String systemPath = FilePathUtil.toSystemSpecificPath(path);
		final File pageDirectory = new File(rootDirectory, systemPath);
		final PageInfo page = loadPage(rootDirectory, path, name, reader, handler);
		final List<IPage> subPages = new LinkedList<IPage>();
		
		page.setPages(subPages);
		
		for (File subDir : pageDirectory.listFiles()) {
			if (subDir.isDirectory()) {
				final String subDirName = subDir.getName();
				final String subPath = path + "/" + subDirName;
				
				subPages.add(loadPages(rootDirectory, subPath, subDirName, reader, handler));
			}
		}
		
		return page;
	}
	
	private PageInfo loadPage(final File rootDirectory, final String path, final String name, final XMLReader reader, final PageInfoHandler handler) throws IOException, SAXException {
		final PageInfo page = new PageInfo(path, name);
		final String systemPath = FilePathUtil.toSystemSpecificPath(path);
		final File pageFile = new File(new File(rootDirectory, systemPath), "page.xml");
		
		if (pageFile.exists()) {
			handler.setPage(page);
			
			try {
				reader.parse(pageFile.getAbsolutePath());
			} catch (InformationCompleteException e) {
			}
		}
		
		return page;
	}
	
	/*private void loadPages(final ISite site, final IPage parent, final URI rootUri, final XMLReader reader, final PageInfoHandler pageInfoHandler) {
		final List<IPage> pages = parent.getPages();
		final String path = parent.getPath();
		final URI directory
		final String relativeDir = FilePathUtil.toSystemSpecificPath(site.getName() + path);
		final File directory = new File(rootDirectory, relativeDir);
		
		if (!directory.exists())
			throw new IllegalStateException(relativeDir + " does not exist");
		
		if (!directory.isDirectory())
			throw new IllegalStateException(relativeDir + " is not a directory");
		
		pages.clear();
		
		for (File subDir : directory.listFiles()) {
			if (subDir.isDirectory()) {
				final String subDirName = subDir.getName();
				final File xmlFile = new File(subDir, "page.xml");
				
				if (xmlFile.exists() && xmlFile.isFile()) {
					final PageInfo pageInfo = new PageInfo(path + subDirName + '/', subDirName);
					
					try {
						pageInfoHandler.setPage(pageInfo);
						reader.parse(xmlFile.getAbsolutePath());
					} catch(InformationCompleteException e) {
					} catch(Exception e) {
						log.error("Cannot read " + xmlFile, e);
						continue;
					}
					
					pages.add(pageInfo);
					loadPages(site, pageInfo, reader, pageInfoHandler); // Call recursively
				}
			}
		}
	}*/
}
