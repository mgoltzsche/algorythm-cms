package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SimpleFileVisitor<T> implements FileVisitor<T> {

	@Override
	public FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(T file, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(T file, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(T dir, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
