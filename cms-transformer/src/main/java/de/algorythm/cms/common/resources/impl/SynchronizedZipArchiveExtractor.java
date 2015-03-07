package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class SynchronizedZipArchiveExtractor implements IArchiveExtractor {

	private final ISourcePathResolver sourceResolver;
	private final Path tmpDirectory;
	private final ZipArchiveUtil zipArchiveUtil = new ZipArchiveUtil();
	
	public SynchronizedZipArchiveExtractor(final ISourcePathResolver sourceResolver, final Path tmpDirectory) {
		this.sourceResolver = sourceResolver;
		this.tmpDirectory = tmpDirectory;
	}
	
	public synchronized Path unzip(final URI zipFileUri) throws ResourceNotFoundException, IOException {
		final Path destinationDirectory = tmpDirectory.resolve(zipFileUri.getPath().substring(1));
		
		if (!Files.exists(destinationDirectory)) {
			final Path zipFile = sourceResolver.resolveSource(zipFileUri);
			
			if (zipFile == null)
				return null;
			
			zipArchiveUtil.unzip(zipFile, destinationDirectory);
		}
		
		return destinationDirectory;
	}
}
