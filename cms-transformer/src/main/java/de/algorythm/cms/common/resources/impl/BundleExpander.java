package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.IModule;
import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.model.entity.bundle.ITheme;
import de.algorythm.cms.common.model.entity.impl.bundle.Bundle;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfig;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

@Singleton
public class BundleExpander implements IBundleExpander {

	private final BundleLoader loader;

	@Inject
	public BundleExpander(BundleLoader loader) {
		this.loader = loader;
	}

	@Override
	public IBundle expandedBundle(final IBundle bundle, final IInputResolver resolver) throws ResourceNotFoundException, IOException, JAXBException {
		final URI uri = bundle.getUri();
		final Bundle result = new Bundle(bundle);
		final Map<Format, IOutputConfig> resultResources = result.getOutputMapping();
		final Map<URI, IBundle> includes = new LinkedHashMap<>();
		final Set<URI> dependencyUris = new HashSet<>();
		
		collectTransitiveDependencies(includes, dependencyUris, bundle, resolver);
		includes.put(uri, bundle);
		
		for (IOutputConfig resources : bundle.getOutputMapping().values())
			resultResources.put(resources.getFormat(), new OutputConfig(resources));
		
		for (IBundle dependency : includes.values())
			extend(result, dependency);
		
		for (IOutputConfig output : result.getOutputMapping().values()) {
			final Format format = output.getFormat();
			final ITheme resultTheme = output.getTheme();
			IOutputConfig resources = bundle.getOutputMapping().get(format);
			
			if (resources == null)
				resources = output;
			
			final ITheme srcTheme = resources.getTheme();
			final Set<URI> alreadyExtended = new HashSet<>();
			
			extendTheme(resultTheme, srcTheme, format, includes, alreadyExtended);
		}
		
		return result;
	}

	private void collectTransitiveDependencies(final Map<URI, IBundle> includes, final Set<URI> dependencyUris, final IBundle bundle, final IInputResolver resolver) throws ResourceNotFoundException, IOException, JAXBException {
		for (URI dependencyUri : bundle.getDependencies()) {
			final URI normalizedUri = dependencyUri.normalize();
			
			if (dependencyUris.add(normalizedUri)) {
				final IBundle dependency = loader.loadBundle(normalizedUri, resolver);
				
				collectTransitiveDependencies(includes, dependencyUris, dependency, resolver);
				includes.put(normalizedUri, dependency);
			}
		}
	}

	private void extend(IBundle bundle, IBundle dependency) {
		final Map<Format, IOutputConfig> map = bundle.getOutputMapping();
		bundle.getSupportedLocales().addAll(dependency.getSupportedLocales());
		
		for (Format format : bundle.getOutputMapping().keySet()) {
			final IOutputConfig src = dependency.getOutputMapping().get(format);
			
			if (src != null) {
				final IModule srcModule = src.getModule();
				final IOutputConfig target = map.get(format);
				
				if (src.getTheme() == null)
					throw new IllegalStateException("Missing theme declaration in " + bundle.getUri() + " for output format " + format);
				
				if (srcModule != null)
					extendModule(target.getModule(), srcModule);
			}
		}
	}

	private void extendModule(IModule module, IModule base) {
		module.getTemplates().addAll(base.getTemplates());
		module.getStyles().addAll(base.getStyles());
		module.getScripts().addAll(base.getScripts());
	}
	
	private void extendTheme(ITheme theme, ITheme base, Format format, Map<URI, IBundle> includes, Set<URI> alreadyExtended) {
		final URI themeBaseUri = base.getBaseTheme();
		
		if (themeBaseUri != null) {
			if (!alreadyExtended.add(themeBaseUri))
				throw new IllegalStateException("Cyclic " + format + " base theme reference to " + themeBaseUri);
			
			final IBundle baseBundle = includes.get(themeBaseUri);
			
			if (baseBundle == null)
				throw new IllegalStateException("Theme base bundle " + themeBaseUri + " is not declared as dependency");
			
			final IOutputConfig baseFormat = baseBundle.getOutputMapping().get(format);
			
			if (baseFormat == null)
				throw new IllegalStateException("Theme base " + themeBaseUri + " does not support output format " + format);
			
			final ITheme baseTheme = baseFormat.getTheme();
			
			if (baseTheme == null)
				throw new IllegalStateException(format + " theme base " + themeBaseUri + " does not declare a theme");
			
			extendTheme(theme, baseTheme, format, includes, alreadyExtended); // Recursion
		}
		
		extendModule(theme, base);
	}
}