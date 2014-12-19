package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IDependency;
import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;
import de.algorythm.cms.common.model.entity.ISchemaSource;
import de.algorythm.cms.common.model.entity.impl.SchemaSource;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IDependencyLoader;

@Singleton
public class BundleExpander implements IBundleExpander {

	private final IDependencyLoader dependencyLoader;
	
	@Inject
	public BundleExpander(final IDependencyLoader dependencyLoader) {
		this.dependencyLoader = dependencyLoader;
	}
	
	@Override
	public IBundle expandBundle(final IBundle bundle) {
		final Set<IBundle> flattened = new LinkedHashSet<IBundle>(); // To perform breadth-first search
		final Set<Path> resolved = new LinkedHashSet<Path>();
		final IBundle expandBundle = bundle.copy();
		
		resolved.add(expandBundle.getLocation());
		
		for (IDependency bundleRef : bundle.getDependencies())
			resolveDependency(expandBundle, bundleRef, flattened, resolved);
		
		while (!flattened.isEmpty()) {
			final Iterator<IBundle> iter = flattened.iterator();
			final IBundle nextBundle = iter.next();
			
			if (expandBundle.getStartPage() == null && nextBundle.getStartPage() != null)
				expandBundle.setStartPage(nextBundle.getStartPage());
			
			iter.remove();
			mergeBundle(nextBundle, expandBundle);
			
			for (IDependency bundleRef : nextBundle.getDependencies())
				resolveDependency(nextBundle, bundleRef, flattened, resolved);
		}
		
		expandBundle.setRootDirectories(Collections.unmodifiableList(new LinkedList<Path>(resolved)));
		
		return expandBundle;
	}
	
	private void resolveDependency(final IBundle bundle,
			final IDependency bundleRef, final Set<IBundle> flattened,
			final Set<Path> resolved) {
		final String bName = bundleRef.getName();
		
		if (bName == null)
			throw new IllegalStateException("Incomplete dependency in '" + bundle.getName() + '\'');
		
		final IBundle dependency = dependencyLoader.loadDependency(bName);
		final Path dependencyDir = dependency.getLocation();
		
		if (resolved.add(dependencyDir) && !flattened.add(dependency))
			throw new IllegalStateException("Cyclic reference between '" + bundle.getName() + "' and '" + bName + '\'');
	}
	
	private void mergeBundle(final IBundle source, final IBundle target) {
		target.getParams().addAll(source.getParams());
		
		final LinkedList<ISchemaSource> schemas = target.getSchemaLocations();
		final Iterator<ISchemaSource> schemaIter = source.getSchemaLocations().descendingIterator();
		
		while (schemaIter.hasNext()) {
			final ISchemaSource next = schemaIter.next();
			final URI nextUri = next.getUri().normalize();
			
			schemas.addFirst(new SchemaSource(nextUri));
		}
		
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
}