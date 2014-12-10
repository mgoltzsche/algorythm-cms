package de.algorythm.cms.common.resources.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	static private final Path ROOT_PATH = Paths.get("/");
	
	private final Collection<Path> unlocalizedRootPathes;
	private final Collection<Path> rootPathes;

	public ResourceResolver(final Collection<Path> unlocalizedRootPathes, final Locale locale) {
		this.unlocalizedRootPathes = unlocalizedRootPathes;
		rootPathes = createLocalizedRootPathes(locale.getLanguage(), "international");
	}

	public ResourceResolver(final IBundle bundle, final Path tmpDirectory) {
		final Set<Path> rootPathSet = new LinkedHashSet<Path>();
		
		rootPathSet.add(bundle.getLocation());
		rootPathSet.add(tmpDirectory);
		
		for (Path rootPath : bundle.getRootDirectories())
			rootPathSet.add(rootPath);
		
		unlocalizedRootPathes = Collections.unmodifiableCollection(new LinkedList<Path>(rootPathSet));
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

	@Override
	public Path resolve(final Path publicPath, final Path systemBasePath) {
		return publicPath.isAbsolute()
			? resolve(publicPath)
			: resolve(toPublicPath(systemBasePath.resolveSibling(publicPath)));
	}

	@Override
	public Path resolve(final Path publicPath) {
		final Path relativePath = publicPath.isAbsolute()
				? ROOT_PATH.relativize(publicPath)
				: publicPath;
		
		for (Path rootPath : rootPathes) {
			final Path systemPath = rootPath.resolve(relativePath.toString()).normalize();
			
			if (Files.exists(systemPath)) {
				if (!systemPath.startsWith(rootPath))
					throw new IllegalArgumentException(systemPath + " is outside root path " + rootPath);
				
				return systemPath;
			}
		}
		
		throw new IllegalStateException("Cannot resolve resource "
				+ publicPath + ". Pathes: \n\t"
				+ Joiner.on("\n\t").join(rootPathes));
	}

	private Path toPublicPath(Path systemPath) {
		systemPath = systemPath.normalize();
		
		for (Path rootPath : rootPathes) {
			if (systemPath.startsWith(rootPath))
				return ROOT_PATH.resolve(rootPath.relativize(systemPath));
		}
		
		throw new IllegalArgumentException("Cannot publish system path "
				+ systemPath + " because it is not contained in any path. "
				+ "Pathes: \n\t" + Joiner.on("\n\t").join(rootPathes));
	}
}
