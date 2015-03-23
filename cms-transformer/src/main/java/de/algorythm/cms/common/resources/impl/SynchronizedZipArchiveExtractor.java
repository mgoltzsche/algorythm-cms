package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class SynchronizedZipArchiveExtractor implements IArchiveExtractor {

	private final ZipArchiveUtil zipArchiveUtil;

	@Inject
	public SynchronizedZipArchiveExtractor(ZipArchiveUtil zipArchiveUtil) {
		this.zipArchiveUtil = zipArchiveUtil;
	}

	public synchronized Path unzip(final URI zipFileUri, final IRenderingContext ctx) throws ResourceNotFoundException, IOException {
		final Path destinationDirectory = ctx.resolveSource(URI.create("tmp:" + zipFileUri.getPath()));
		
		if (!Files.exists(destinationDirectory)) {
			final Path zipFile = ctx.resolveSource(zipFileUri);
			
			if (zipFile == null)
				return null;
			
			zipArchiveUtil.unzip(zipFile, destinationDirectory);
		}
		
		return destinationDirectory;
	}
}
