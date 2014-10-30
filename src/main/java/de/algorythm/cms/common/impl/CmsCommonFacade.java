package de.algorythm.cms.common.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
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
		final File siteDirectory = new File(repositoryDirectory, site.getName());
		final File outputDirectory = new File(siteDirectory, "generated");
		
		if (outputDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(outputDirectory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		outputDirectory.mkdirs();
		
		try {
			renderer.render(site, outputDirectory);
		} catch (RendererException e) {
			throw new RuntimeException("Cannot render page", e);
		}
	}
}
