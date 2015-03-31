package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import de.algorythm.cms.common.resources.IInputSource;

public class ClasspathInputSource implements IInputSource {

	private final String resourceName;
	private final String name;
	private final Date date;

	public ClasspathInputSource(String resourceName, Date date) {
		this.resourceName = resourceName;
		this.name = toName(resourceName);
		this.date = date;
	}

	private String toName(String path) {
		final int slashPos = path.charAt(path.length() - 1) == '/'
				? path.lastIndexOf('/', path.length() - 2)
				: path.lastIndexOf('/');
		
		return path.substring(slashPos == -1 ? 0 : slashPos);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getCreationTime() {
		return date;
	}

	@Override
	public Date getLastModifiedTime() {
		return date;
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return getClass().getResourceAsStream(resourceName);
	}
}
