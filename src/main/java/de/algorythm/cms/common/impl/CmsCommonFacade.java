package de.algorythm.cms.common.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;
import de.algorythm.cms.common.model.index.ISiteIndex;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final File repositoryDirectory;
	private final ISiteIndex siteIndex;
	private final PagesXmlGenerator pagesXmlGenerator;
	private final IContentRenderer renderer;
	
	@Inject
	public CmsCommonFacade(final Configuration cfg, final ISiteIndex siteIndex, final PagesXmlGenerator pagesXmlGenerator, final IContentRenderer renderer) throws IOException {
		if (!cfg.repository.isDirectory())
			throw new IOException(cfg.repository + " is not a valid directory");
		
		this.repositoryDirectory = cfg.repository;
		this.siteIndex = siteIndex;
		this.pagesXmlGenerator = pagesXmlGenerator;
		this.renderer = renderer;
	}
	
	@Override
	public List<ISite> listSites() {
		try {
			return siteIndex.getSites();
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generatePagesXml(final ISite site) {
		try {
			pagesXmlGenerator.generatePagesXml(site);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generateSite(final ISite site) {
		final String siteName = site.getName();
		final String generatedDirStr = repositoryDirectory.getAbsolutePath() + File.separator + siteName + File.separator + "generated";
		final File generatedDir = new File(generatedDirStr);
		
		if (generatedDir.exists()) {
			try {
				FileUtils.deleteDirectory(generatedDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		generatedDir.mkdirs();
		
		new File(generatedDir, "html").mkdir();
		
		generatePages(site, site.getStartPage(), generatedDirStr);
	}
	
	private void generatePages(final ISite site, final IPage page, final String generatedDir) {
		final String path = page.getPath();
		final File directory = new File(generatedDir + File.separator + "html" + File.separator + path.replaceAll("/", File.separator));
		final File htmlFile = new File(directory, "index.html");
		final FileWriter writer;
		
		directory.mkdir();
		
		try {
			writer = new FileWriter(htmlFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot create file writer for " + htmlFile, e);
		}
		
		try {
			renderer.render(site, page, writer);
		} catch (RendererException e) {
			throw new RuntimeException("Cannot render page", e);
		}
		
		for (IPage child : page.getPages())
			generatePages(site, child, generatedDir);
	}
}
