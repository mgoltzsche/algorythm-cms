package de.algorythm.cms.common.impl;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.scheduling.IFuture;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final IBundleLoader bundleLoader;
	private final IBundleExpander expander;
	private final IRenderer renderer;
	
	@Inject
	public CmsCommonFacade(final IBundleLoader bundleLoader, final IBundleExpander expander, final IRenderer renderer) throws IOException {
		this.bundleLoader = bundleLoader;
		this.expander = expander;
		this.renderer = renderer;
	}
	
	@Override
	public IBundle loadBundle(final Path bundleXml) {
		try {
			return bundleLoader.getBundle(bundleXml);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot load bundle in '" + bundleXml + "'. " + e.getMessage(), e);
		}
	}

	@Override
	public IFuture<Void> render(final IBundle bundle, final Path outputDirectory) {
		final FileSystem tmpFs = Jimfs.newFileSystem(Configuration.unix());
		final Path tempDirectory = tmpFs.getPath("/");
		final IBundle expandedBundle = expander.expandBundle(bundle);
		
		try {
			if (Files.exists(outputDirectory))
				deleteDirectory(outputDirectory);
			
			Files.createDirectories(outputDirectory);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return renderer.render(expandedBundle, tempDirectory, outputDirectory);
	}
	
	private void deleteDirectory(final Path directory) throws IOException {
		Files.walkFileTree(directory, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				throw exc;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				Files.delete(dir);
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
