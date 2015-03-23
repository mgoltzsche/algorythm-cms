package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.algorythm.cms.common.resources.IOutputTarget;

public class FileOutputTarget implements IOutputTarget {

	private final String publicPath;
	private final Path file;
	
	public FileOutputTarget(final String publicPath, final Path file) {
		this.publicPath = publicPath;
		this.file = file;
	}

	@Override
	public String getPublicPath() {
		return publicPath;
	}

	@Override
	public OutputStream createOutputStream() throws IOException {
		return Files.newOutputStream(file);
	}
}
