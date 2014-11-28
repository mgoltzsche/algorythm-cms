package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IDependency;
import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfiguration;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.IResourceResolver;

public class ResourceResolver implements IResourceResolver {

	static private final Logger log = LoggerFactory.getLogger(ResourceResolver.class);
	
	private final Collection<URI> rootPathes;
	private final IBundle mergedBundle;
	
	public ResourceResolver(final IBundle bundle, final File tmpDirectory, final IDependencyLoader dependencyLoader) {
		final LinkedHashSet<URI> rootPathSet = new LinkedHashSet<URI>();
		rootPathSet.add(bundle.getLocation());
		rootPathSet.add(tmpDirectory.toURI());
		
		final LinkedHashSet<IBundle> flattenedBundles = new LinkedHashSet<IBundle>();
		
		flattenedBundles.add(bundle);
		
		mergedBundle = bundle.copy();
		
		do {
			final Iterator<IBundle> iter = flattenedBundles.iterator();
			final IBundle nextBundle = iter.next();
			iter.remove();
			
			rootPathSet.add(nextBundle.getLocation());
			mergeBundle(nextBundle, mergedBundle);
			
			for (IDependency bundleRef : nextBundle.getDependencies()) {
				final String bName = bundleRef.getName();
				
				if (bName == null)
					throw new IllegalStateException("Incomplete dependency in '" + bundle.getName() + '\'');
				
				final IBundle dependency = dependencyLoader.loadDependency(bName);
				
				if (!flattenedBundles.add(dependency))
					throw new IllegalStateException("Cyclic reference between '" + nextBundle.getName() + "' and '" + bName + '\'');
			}
		} while (!flattenedBundles.isEmpty());
		
		rootPathes = Collections.unmodifiableCollection(rootPathSet);
	}
	
	public IBundle getMergedBundle() {
		return mergedBundle;
	}
	
	private void mergeBundle(final IBundle source, final IBundle target) {
		final Set<IParam> mergedProperties = target.getParams();
		
		for (IOutputConfiguration output : source.getOutput()) {
			if (target.containsOutput(output)) { // Merge output cfg
				final IOutputConfiguration mergedOutput = target.getOutput(output.getId());
				
				mergeJobs(mergedOutput.getJobs(), output.getJobs());
			} else { // Add new output cfg
				target.addOutput(output.copy());
			}
		}
		
		mergedProperties.addAll(source.getParams());
	}
	
	private void mergeJobs(final Set<IRenderingJobConfiguration> mergedJobs, final Set<IRenderingJobConfiguration> jobs) {
		for (IRenderingJobConfiguration job : jobs) {
			if (!mergedJobs.add(job)) {
				IRenderingJobConfiguration mergeJob = null;
				
				for (IRenderingJobConfiguration mergedJob : mergedJobs) {
					if (mergedJob.equals(job)) {
						mergeJob = mergedJob;
						break;
					}
				}
				
				mergeJob.getParams().addAll(job.getParams());
			}
		}
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
	public URI toSystemUri(final URI absolutePublicUri) throws FileNotFoundException {
		final URI bundleUri = absolutePublicUri;
		final String relativeBundleUriPath = bundleUri.getPath().substring(1);
		
		for (URI rootPath : rootPathes) {
			final URI systemUri = rootPath.resolve(relativeBundleUriPath).normalize();
			final File resolvedFile = new File(systemUri);
			
			if (resolvedFile.exists()) {
				if (!systemUri.getPath().startsWith(rootPath.getPath()))
					throw new IllegalArgumentException(systemUri + " is outside root path " + rootPath);
				
				return systemUri;
			}
		}
		
		throw new FileNotFoundException("Cannot resolve resource URI "
				+ absolutePublicUri + ". Pathes: \n\t"
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
