package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Joiner;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IUriResolver;

public class ResourceResolver implements IUriResolver {

	//static private final Path ROOT_PATH = Paths.get("/");
	
	private final Collection<Path> unlocalizedRootPathes;
	private final Collection<Path> rootPathes;

	public ResourceResolver(final Collection<Path> unlocalizedRootPathes, final Locale locale) {
		this.unlocalizedRootPathes = unlocalizedRootPathes;
		rootPathes = createLocalizedRootPathes(locale.getLanguage(), "international");
	}

	public ResourceResolver(final IBundle bundle, final Path tmpDirectory) {
		final Set<Path> rootPathSet = new LinkedHashSet<Path>();
		
		rootPathSet.add(tmpDirectory);
		rootPathSet.add(bundle.getLocation());
		
		for (Path rootPath : bundle.getRootDirectories())
			rootPathSet.add(rootPath);
		
		unlocalizedRootPathes = new LinkedList<Path>(rootPathSet);
		rootPathes = createLocalizedRootPathes("international");
	}

	private Collection<Path> createLocalizedRootPathes(final String... localePrefixes) {
		final Collection<Path> localizedRootPathes = new LinkedHashSet<Path>();
		
		for (Path rootPath : unlocalizedRootPathes)
			for (String localePrefix : localePrefixes)
				localizedRootPathes.add(rootPath.resolve(localePrefix));
		
		return Collections.unmodifiableCollection(localizedRootPathes);
	}

	@Override
	public IUriResolver createLocalizedResolver(final Locale locale) {
		return new ResourceResolver(unlocalizedRootPathes, locale);
	}

	@Override
	public Collection<Path> getRootPathes() {
		return rootPathes;
	}

	/*@Override
	public Path resolve(final URI publicPath, final URI systemBasePath) {
		final String path = publicPath.getPath();
		
		return path.length() > 0 && path.charAt(0) == '/'
			? resolve(publicPath)
			: resolve(toPublicPath(systemBasePath.resolve(publicPath)));
	}*/

	@Override
	public Path resolve(final URI publicUri) {
		final String path = publicUri.normalize().getPath();
		final String relativePath = path.length() > 0 && path.charAt(0) == '/'
			? path.substring(1) : path;
		
		if (relativePath.length() > 2 && relativePath.startsWith("../") ||
				relativePath.startsWith(".."))
			throw new IllegalAccessError("Bundle parent directory access denied: " + publicUri);
		
		for (Path rootPath : rootPathes) {
			final Path systemPath = rootPath.resolve(relativePath);
			
			if (Files.exists(systemPath))
				return systemPath;
		}
		
		throw new IllegalStateException("Cannot resolve resource URI "
				+ publicUri + ". Pathes: \n\t"
				+ Joiner.on("\n\t").join(rootPathes));
	}
	
	/*@Override
	public boolean exists(final Path publicPath, final Locale locale) {
		final String relativePath = relativizeBundlePath(publicPath).toString();
		
		for (Path rootPath : unlocalizedRootPathes) {
			final Path systemPath = rootPath.resolve("de/" + publicPath).resolve(relativePath.toString()).normalize();
			
			if (Files.exists(systemPath)) {
				
			}
		}
	}
	
	private Path toPublicPath(Path systemPath) {
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
