package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import de.algorythm.cms.common.resources.IInputSource;

public class FileInputSource implements IInputSource {

	private final Path file;
	private final String name;
	private final Date creationTime;
	private final Date lastModifiedTime;

	public FileInputSource(Path file) throws IOException {
		this.file = file;
		name = file.getFileName().toString();
		final BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
		creationTime = new Date(attr.creationTime().toMillis());
		lastModifiedTime = new Date(attr.lastModifiedTime().toMillis());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return Files.newInputStream(file);
	}
}
