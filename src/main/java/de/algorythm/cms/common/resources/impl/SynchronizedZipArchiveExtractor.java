package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class SynchronizedZipArchiveExtractor implements IArchiveExtractor {

	private final ISourcePathResolver sourceResolver;
	private final Path tmpDirectory;
	
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
			
			unzip(zipFile, destinationDirectory);
		}
		
		return destinationDirectory;
	}
	
	private void unzip(final Path zipFile, final Path destinationDirectory)
			throws IOException {
		Files.createDirectories(destinationDirectory);
		
		try (InputStream stream = Files.newInputStream(zipFile);
				ZipInputStream zipStream = new ZipInputStream(stream)) {
			ZipEntry entry = zipStream.getNextEntry();
			
			while (entry != null) {
				System.out.println("## " + entry.getName());
				if (!entry.getName().isEmpty()) {
				final Path dest = destinationDirectory.resolve(entry.getName());
				
				if (entry.isDirectory()) {
					Files.createDirectories(dest);
				} else {
					Files.createDirectories(dest.getParent());
					Files.copy(zipStream, dest);
				}
				}
				entry = zipStream.getNextEntry();
			}
		}
	}
}
