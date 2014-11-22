package de.algorythm.cms.common.impl;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.generator.PagesXmlGenerator;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.RendererException;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final IDependencyLoader dependencyLoader;
	private final IBundleLoader bundleLoader;
	private final PagesXmlGenerator pagesXmlGenerator;
	private final IContentRenderer renderer;
	
	@Inject
	public CmsCommonFacade(final IBundleLoader bundleLoader, final IDependencyLoader dependencyLoader, final PagesXmlGenerator pagesXmlGenerator, final IContentRenderer renderer) throws IOException {
		this.dependencyLoader = dependencyLoader;
		this.bundleLoader = bundleLoader;
		this.pagesXmlGenerator = pagesXmlGenerator;
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
		try {
			pagesXmlGenerator.generatePagesXml(bundle, outputDirectory);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot generate page.xml of site '" + bundle.getName() + "'. " + e.getMessage(), e);
		}
	}
	
	@Override
	public void generateSite(final IBundle bundle, final File tmpDirectory, final File outputDirectory) {
		final ResourceResolver resolver = new ResourceResolver(bundle, tmpDirectory, dependencyLoader);
		
		try {
			renderer.render(resolver.getMergedBundle(), resolver, outputDirectory);
		} catch (RendererException e) {
			throw new RuntimeException("Cannot render site '" + bundle.getName() + "'. " + e.getMessage(), e);
		}
	}
}
