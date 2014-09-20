package de.algorythm.cms.common.model.dao.impl.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.util.FilePathUtil;

public class XmlResourceDao {

	static private final Logger log = LoggerFactory.getLogger(XmlResourceDao.class);
	
	private final Locale defaultLocale;
	private final File repositoryDirectory;
	private final IXmlReaderFactory saxReaderFactory;
	private final LocaleResolver locales;
	
	public XmlResourceDao(final Configuration cfg, final LocaleResolver locales, final IXmlReaderFactory readerFactory) {
		repositoryDirectory = cfg.repository;
		defaultLocale = cfg.defaultLanguage;
		this.locales = locales;
		this.saxReaderFactory = readerFactory;
	}
	
	public List<ISite> getSites() throws SAXException {
		final File[] rootFiles = repositoryDirectory.listFiles();
		final ArrayList<ISite> sites = new ArrayList<ISite>(rootFiles.length);
		final IncludingHandler inclHandler = new IncludingHandler(saxReaderFactory);
		final SiteInfoHandler siteInfoHandler = new SiteInfoHandler(locales, defaultLocale);
		final PageInfoHandler pageInfoHandler = new PageInfoHandler();
		final XMLReader reader = saxReaderFactory.createReader();
		
		reader.setContentHandler(inclHandler);
		reader.setErrorHandler(inclHandler);
		
		for (File siteDir : rootFiles) {
			if (siteDir.isDirectory()) {
				final String siteName = siteDir.getName();
				final SiteInfo site = new SiteInfo(siteName);
				final PageInfo page = new PageInfo("/", siteName);
				final File startPageXmlFile = new File(siteDir, "/pages/page.xml");
				
				siteInfoHandler.setSite(site);
				siteInfoHandler.setPage(page);
				
				if (startPageXmlFile.isFile()) {
					inclHandler.setDelegator(siteInfoHandler);
					
					try {
						reader.parse(startPageXmlFile.getAbsolutePath());
					} catch (InformationCompleteException e) {
					} catch (Exception e) {
						log.error("Cannot read start page " + startPageXmlFile, e);
						continue;
					}
					
					site.setStartPage(page);
					sites.add(site);
					inclHandler.setDelegator(pageInfoHandler);
					loadPages(site, page, reader, pageInfoHandler);
				}
			}
		}
		
		sites.trimToSize();
		Collections.sort(sites);
		
		return sites;
	}
	
	private void loadPages(final ISite site, final IPage parent, final XMLReader reader, final PageInfoHandler pageInfoHandler) {
		final List<IPage> pages = parent.getPages();
		final String path = parent.getPath();
		final String relativeDir = FilePathUtil.toSystemSpecificPath(site.getName() + "/pages" + path);
		final File directory = new File(repositoryDirectory, relativeDir);
		
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
	}
}
