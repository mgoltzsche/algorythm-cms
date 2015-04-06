package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class SynchronizedZipArchiveExtractor implements IArchiveExtractor {

	private final ZipArchiveUtil zipArchiveUtil;

	@Inject
	public SynchronizedZipArchiveExtractor(ZipArchiveUtil zipArchiveUtil) {
		this.zipArchiveUtil = zipArchiveUtil;
	}

	@Override
	public synchronized Path unzip(final URI zipFileUri, final IInputResolver resolver, final IWriteableResources tmp) throws ResourceNotFoundException, IOException {
		final Path destinationDirectory = tmp.resolvePublicPath(zipFileUri.getPath());
		
		if (!Files.exists(destinationDirectory)) {
			final InputStream zipStream = resolver.createInputStream(zipFileUri);
			
			if (zipStream == null)
				throw new IllegalStateException("Cannot find ZIP archive at " + zipFileUri);
			
			zipArchiveUtil.unzip(zipStream, destinationDirectory);
		}
		
		return destinationDirectory;
	}
}
