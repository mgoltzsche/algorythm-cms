package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class ResourceResolver implements ISourcePathResolver {

	private final Collection<Path> rootPathes;
	
	public ResourceResolver(final IBundle bundle, final Path tmpDirectory) {
		final Set<Path> rootPathSet = new LinkedHashSet<Path>();
		
		rootPathSet.add(tmpDirectory);
		rootPathSet.add(bundle.getLocation());
		
		for (Path rootPath : bundle.getRootDirectories())
			rootPathSet.add(rootPath);
		
		rootPathes = new LinkedList<Path>(rootPathSet);
	}

	@Override
	public Path resolveSource(final URI publicUri) throws ResourceNotFoundException {
		final String path = publicUri.normalize().getPath();
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1) : path;
		
		for (Path rootPath : rootPathes) {
			final Path systemPath = rootPath.resolve(relativePath);
			
			if (Files.exists(systemPath)) {
				if (!systemPath.toString().startsWith(rootPath.toString()))
					throw new IllegalAccessError("Bundle parent directory access denied: " + publicUri);
				
				return systemPath;
			}
		}
		
		throw new ResourceNotFoundException("Cannot resolve " + publicUri);
	}

	/*private Path toPublicPath(Path systemPath) {
		systemPath = systemPath.normalize();
		
		for (Path rootPath : rootPathes) {
			if (systemPath.startsWith(rootPath))
				return rootPath.resolve(rootPath.relativize(systemPath));
		}
		
		throw new IllegalArgumentException("Cannot publish system path "
				+ systemPath + " because it is not contained in any path. "
				+ "Pathes: \n\t" + Joiner.on("\n\t").join(rootPathes));
	}*/
}
