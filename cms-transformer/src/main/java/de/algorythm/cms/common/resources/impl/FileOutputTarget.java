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
	public OutputStream createOutputStream() throws IOException {
		Files.createDirectories(file.getParent());
		
		return Files.newOutputStream(file);
	}

	@Override
	public boolean exists() {
		return Files.exists(file);
	}
}
