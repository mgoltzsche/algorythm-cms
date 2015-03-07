package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchiveUtil {

	public void unzip(final Path zipFile, final Path destinationDirectory)
			throws IOException {
		Files.createDirectories(destinationDirectory);
		InputStream stream = Files.newInputStream(zipFile);
		
		try (ZipInputStream zipStream = new ZipInputStream(stream)) {
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
