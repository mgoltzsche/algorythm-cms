package de.algorythm.cms.common.rendering.pipeline.impl;

import static de.algorythm.cms.common.rendering.pipeline.impl.TransformationContextInitializationUtil.createTransformationTemplates;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

import net.sf.saxon.Controller;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.ITransformationContext;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsInputURIResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsOutputURIResolver;

public class TransformationContext implements ITransformationContext {

	private final IUriResolver resourceResolver;
	private final IOutputUriResolver outputResolver;
	private final Templates templates;
	private final URIResolver uriResolverAdapter;
	private final OutputURIResolver outputUriResolverAdapter;
	private final URI notFoundContent;
	
	public TransformationContext(final IRenderingContext processCtx, final Collection<URI> xslSources, final URI notFoundContent) {
		this(createTransformationTemplates(xslSources, processCtx.getResourceResolver(), notFoundContent),
			processCtx.getResourceResolver(), processCtx.getOutputResolver(), notFoundContent);
	}
	
	private TransformationContext(final Templates templates, final IUriResolver uriResolver, final IOutputUriResolver outputUriResolver, final URI notFoundContent) {
		this.resourceResolver = uriResolver;
		this.outputResolver = outputUriResolver;
		this.templates = templates;
		this.notFoundContent = notFoundContent;
		uriResolverAdapter = new CmsInputURIResolver(uriResolver, notFoundContent);
		outputUriResolverAdapter = new CmsOutputURIResolver(outputUriResolver);
	}
	
	@Override
	public TransformationContext createLocalized(final Locale locale, final boolean localizedOutput) {
		final IUriResolver inResolver = resourceResolver.createLocalizedResolver(locale);
		final IOutputUriResolver outResolver = localizedOutput
				? outputResolver.createLocalizedResolver(locale)
				: outputResolver;
		
		return new TransformationContext(templates, inResolver, outResolver, notFoundContent);
	}
	
	@Override
	public Transformer createTransformer() {
		try {
			final Transformer transformer = templates.newTransformer();
			final Controller trnsfrmCtrl = ((TransformerImpl) transformer).getUnderlyingController();
			
			transformer.setURIResolver(uriResolverAdapter);
			trnsfrmCtrl.setOutputURIResolver(outputUriResolverAdapter);
			
			return transformer;
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Cannot create transformer. " + e, e);
		}
	}

	@Override
	public IUriResolver getResourceResolver() {
		return resourceResolver;
	}

	@Override
	public IOutputUriResolver getOutputUriResolver() {
		return outputResolver;
	}
	
	/*public void transform(final Path source, final Path target) throws IOException, TransformerException {
		final XMLReader xmlReader = createReader();
		final Transformer transformer = createTransformer();
		final Reader fileReader = Files.newBufferedReader(source, StandardCharsets.UTF_8);
		final Writer writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8);
		final InputSource src = new InputSource(fileReader);
		final Source xmlSource = new SAXSource(xmlReader, src);
		final StreamResult xmlResult = new StreamResult(writer);
		
		xmlSource.setSystemId(source.toString());
		xmlResult.setSystemId(target.toString());
		transformer.transform(xmlSource, xmlResult);
	}*/
}
