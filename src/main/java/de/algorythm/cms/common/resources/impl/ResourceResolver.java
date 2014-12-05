package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Joiner;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IDependency;
import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.IResourceResolver;

public class ResourceResolver implements IResourceResolver {

	private final IBundle mergedBundle;
	private final Collection<URI> unlocalizedRootPathes;
	private final Collection<URI> rootPathes;
	
	private ResourceResolver(final Collection<URI> unlocalizedRootPathes, final IBundle mergedBundle, final Locale locale) {
		this.mergedBundle = mergedBundle;
		this.unlocalizedRootPathes = unlocalizedRootPathes;
		rootPathes = createLocalizedRootPathes(locale.getLanguage(), "international");
	}
	
	public ResourceResolver(final IBundle bundle, final File tmpDirectory, final IDependencyLoader dependencyLoader) {
		final LinkedHashSet<URI> rootPathSet = new LinkedHashSet<URI>();
		rootPathSet.add(bundle.getLocation());
		rootPathSet.add(tmpDirectory.toURI());
		
		final LinkedHashSet<IBundle> flattenedBundles = new LinkedHashSet<IBundle>(); // To perform breadth-first search
		final Set<IBundle> includedBundles = new HashSet<IBundle>(); // To not include duplicates
		
		mergedBundle = bundle.copy();
		
		for (IDependency bundleRef : bundle.getDependencies())
			resolveDependency(bundle, bundleRef, flattenedBundles, includedBundles, dependencyLoader);
		
		while (!flattenedBundles.isEmpty()) {
			final Iterator<IBundle> iter = flattenedBundles.iterator();
			final IBundle nextBundle = iter.next();
			
			iter.remove();
			rootPathSet.add(nextBundle.getLocation());
			mergeBundle(nextBundle, mergedBundle);
			
			for (IDependency bundleRef : nextBundle.getDependencies())
				resolveDependency(nextBundle, bundleRef, flattenedBundles, includedBundles, dependencyLoader);
		}
		
		unlocalizedRootPathes = Collections.unmodifiableCollection(rootPathSet);
		rootPathes = createLocalizedRootPathes("international");
	}
	
	private Collection<URI> createLocalizedRootPathes(final String... localePrefixes) {
		final Collection<URI> localizedRootPathes = new LinkedHashSet<URI>();
		
		for (URI rootPath : unlocalizedRootPathes)
			for (String localePrefix : localePrefixes)
				localizedRootPathes.add(URI.create(rootPath + localePrefix + '/'));
		
		return Collections.unmodifiableCollection(localizedRootPathes);
	}
	
	private void resolveDependency(final IBundle bundle, final IDependency bundleRef, final Set<IBundle> flattenedBundles, final Set<IBundle> includedBundles, final IDependencyLoader dependencyLoader) {
		final String bName = bundleRef.getName();
		
		if (bName == null)
			throw new IllegalStateException("Incomplete dependency in '" + bundle.getName() + '\'');
		
		final IBundle dependency = dependencyLoader.loadDependency(bName);
		
		if (includedBundles.add(dependency) && !flattenedBundles.add(dependency))
			throw new IllegalStateException("Cyclic reference between '" + bundle.getName() + "' and '" + bName + '\'');
	}
	
	@Override
	public IResourceResolver createLocalizedResolver(final Locale locale) {
		return new ResourceResolver(unlocalizedRootPathes, mergedBundle, locale);
	}
	
	@Override
	public IBundle getMergedBundle() {
		return mergedBundle;
	}
	
	private void mergeBundle(final IBundle source, final IBundle target) {
		target.getParams().addAll(source.getParams());
		
		for (IOutputConfig output : source.getOutput()) {
			if (target.containsOutput(output)) { // Merge output cfg
				final IOutputConfig mergedOutput = target.getOutput(output.getId());
				
				mergeJobs(mergedOutput.getJobs(), output.getJobs());
			} else { // Add new output cfg
				target.addOutput(output.copy());
			}
		}
	}
	
	private void mergeJobs(final Set<IRenderingJobConfig> mergedJobs, final Set<IRenderingJobConfig> jobs) {
		for (IRenderingJobConfig job : jobs) {
			if (!mergedJobs.add(job)) {
				IRenderingJobConfig mergeJob = null;
				
				for (IRenderingJobConfig mergedJob : mergedJobs) {
					if (mergedJob.equals(job)) {
						mergeJob = mergedJob;
						break;
					}
				}
				
				final LinkedList<IParam> mergeParams = mergeJob.getParams();
				final Iterator<IParam> paramIter = job.getParams().descendingIterator();
				
				while (paramIter.hasNext())
					mergeParams.addFirst(paramIter.next());
			}
		}
	}
	
	@Override
	public Collection<URI> getRootPathes() {
		return rootPathes;
	}
	
	@Override
	public URI toSystemUri(final URI publicHref, final URI systemBaseUri) throws FileNotFoundException {
		final URI resolvedSystemUri = systemBaseUri.resolve(publicHref.getPath());
		final URI resolvedPublicUri = publicHref.getPath().equals(resolvedSystemUri.getPath())
				? publicHref
				: toPublicUri(resolvedSystemUri);
		
		return toSystemUri(resolvedPublicUri);
	}
	
	@Override
	public URI toSystemUri(final URI publicUri) throws FileNotFoundException {
		final String path = publicUri.getPath();
		final String relativePath = path.isEmpty()
				? path
				: (path.charAt(0) == '/' ? path.substring(1) : path);
		
		for (URI rootPath : rootPathes) {
			final URI systemUri = rootPath.resolve(relativePath).normalize();
			final File resolvedFile = new File(systemUri);
			
			if (resolvedFile.exists()) {
				if (!systemUri.getPath().startsWith(rootPath.getPath()))
					throw new IllegalArgumentException(systemUri + " is outside root path " + rootPath);
				
				return systemUri;
			}
		}
		
		throw new FileNotFoundException("Cannot resolve resource URI "
				+ publicUri + ". Pathes: \n\t"
				+ Joiner.on("\n\t").join(rootPathes));
	}
	
	private URI toPublicUri(final URI systemUri) {
		final String systemUriStr = systemUri.normalize().getPath();
		
		for (URI rootUri : rootPathes) {
			final String rootPath = rootUri.getPath();
			
			if (systemUriStr.startsWith(rootPath))
				return URI.create(systemUriStr.substring(rootPath.length() - 1));
		}
		
		throw new IllegalArgumentException("Cannot publish system URI "
				+ systemUri + " because it is not contained in any path. "
				+ "Pathes: \n\t" + Joiner.on("\n\t").join(rootPathes));
	}
}
