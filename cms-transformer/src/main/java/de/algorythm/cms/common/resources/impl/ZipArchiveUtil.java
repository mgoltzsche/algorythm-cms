package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchiveUtil {

	/**
	 * Extracts the given ZIP file's contents from stream and writes them into destinationDirectory.
	 * The given stream is closed afterwards.
	 * @param zipFileStream Streamed ZIP file
	 * @param destinationDirectory Extraction target directory
	 * @throws IOException
	 */
	public void unzip(final InputStream zipFileStream, final Path destinationDirectory)
			throws IOException {
		Files.createDirectories(destinationDirectory);
		
		try (ZipInputStream zipStream = new ZipInputStream(zipFileStream)) {
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
