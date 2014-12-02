package de.algorythm.cms.common.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;

import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RenderingException;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.impl.ResourceResolver;
import de.algorythm.cms.common.scheduling.IFuture;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final IDependencyLoader dependencyLoader;
	private final IBundleLoader bundleLoader;
	private final PagesXmlGenerator pagesXmlGenerator;
	private final IContentRenderer oldRenderer;
	private final IRenderer renderer;
	
	@Inject
	public CmsCommonFacade(final IBundleLoader bundleLoader, final IDependencyLoader dependencyLoader, final PagesXmlGenerator pagesXmlGenerator, final IContentRenderer oldRenderer, final IRenderer renderer) throws IOException {
		this.dependencyLoader = dependencyLoader;
		this.bundleLoader = bundleLoader;
		this.pagesXmlGenerator = pagesXmlGenerator;
		this.oldRenderer = oldRenderer;
		this.renderer = renderer;
	}
	
	@Override
	public IBundle loadBundle(final File bundleXml) {
		try {
			return bundleLoader.getBundle(bundleXml);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot load bundle in '" + bundleXml + "'. " + e.getMessage(), e);
		}
	}
	
	@Override
	public void generatePagesXml(final IBundle bundle, final File outputDirectory) {
		final IPage startPage = bundleLoader.loadPages(bundle);
		
		try {
			pagesXmlGenerator.generatePagesXml(startPage, outputDirectory);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot generate page.xml of site '" + bundle.getName() + "'. " + e.getMessage(), e);
		}
	}
	
	@Override
	public void generateSite(final IBundle bundle, final File tmpDirectory, final File outputDirectory) {
		final ResourceResolver resolver = new ResourceResolver(bundle, tmpDirectory, dependencyLoader);
		final IPage startPage = bundleLoader.loadPages(bundle);
		
		try {
			oldRenderer.render(resolver.getMergedBundle(), startPage, resolver, outputDirectory);
		} catch (RenderingException e) {
			throw new RuntimeException("Cannot render site '" + bundle.getName() + "'. " + e.getMessage(), e);
		}
	}

	@Override
	public IFuture render(final IBundle bundle, final File outputDirectory) {
		final String tmpDirName = "algorythm-cms-" + bundle.getName() + '-' + new Date().getTime();
		final File tmpDirectory = new File(System.getProperty("java.io.tmpdir", null), tmpDirName);
		
		try {
			if (!tmpDirectory.mkdir())
				throw new IOException("Cannot create temp directory " + tmpDirectory);
			
			if (outputDirectory.exists())
				FileUtils.deleteDirectory(outputDirectory);
			
			if (!outputDirectory.mkdirs())
				throw new IOException("Cannot create output directory " + outputDirectory);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		final ResourceResolver resourceResolver = new ResourceResolver(bundle, tmpDirectory, dependencyLoader);
		
		return renderer.render(resourceResolver, tmpDirectory, outputDirectory, resourceResolver.getMergedBundle().getOutput());
	}
}
