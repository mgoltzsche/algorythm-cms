package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ResourcePathIndex {

	static private class RootPathFile {
		
		public final URI rootPath;
		public final File directory;
		
		public RootPathFile(final URI rootPath, final File directory) {
			this.rootPath = rootPath;
			this.directory = directory;
		}
	}
	
	private final Map<URI, File> uriFileMap;
	
	public ResourcePathIndex(final Collection<URI> rootPathes, final URI relativePath) {
		final Map<URI, File> uriFileMap = new HashMap<URI, File>();
		final LinkedList<RootPathFile> directories = new LinkedList<RootPathFile>();
		
		for (URI rootPath : rootPathes) {
			final File subDir = new File(rootPath.resolve(relativePath.getPath().substring(1)));
			
			if (subDir.exists() && subDir.isDirectory())
				directories.add(new RootPathFile(rootPath, subDir));
		}
		
		while (!directories.isEmpty()) {
			final RootPathFile dir = directories.poll();
			
			for (File file : dir.directory.listFiles()) {
				if (file.isDirectory()) {
					directories.add(new RootPathFile(dir.rootPath, file));
				} else {
					final URI relUri = dir.rootPath.relativize(file.toURI());
					final URI publicUri = URI.create('/' + relUri.getPath());
					
					if (!uriFileMap.containsKey(publicUri))
						uriFileMap.put(publicUri, file);
				}
			}
		}
		
		this.uriFileMap = Collections.unmodifiableMap(uriFileMap);
	}
	
	public Map<URI, File> getUriFileMap() {
		return uriFileMap;
	}
}