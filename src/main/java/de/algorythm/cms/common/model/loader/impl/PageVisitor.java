package de.algorythm.cms.common.model.loader.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;

import org.xml.sax.SAXException;

import com.google.common.base.Function;

import de.algorythm.cms.common.model.entity.IPage;

public class PageVisitor implements FileVisitor<Path> {
	
	static public interface IPageVisitor {
		void visit(Path file);
	}
	
	private final IPageVisitor callback;
	
	public PageVisitor(final IPageVisitor callback) {
		this.callback = callback;
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir,
			BasicFileAttributes attrs) throws IOException {
		final Path pageFile = dir.resolve("page.xml");
		
		if (Files.exists(pageFile)) {
			callback.visit(pageFile);
			
			return FileVisitResult.CONTINUE;
		} else {
			return FileVisitResult.SKIP_SUBTREE;
		}
	}

	@Override
	public FileVisitResult visitFile(Path file,
			BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file,
			IOException exc) throws IOException {
		throw exc;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir,
			IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
