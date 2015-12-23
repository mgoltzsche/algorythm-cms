package de.algorythm.cms.expath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;

import de.algorythm.cms.expath.handler.ResourceHandler;
import de.algorythm.cms.expath.handler.XQueryModuleHandler;
import de.algorythm.cms.expath.handler.XsltModuleHandler;
import de.algorythm.cms.expath.model.AbstractComponent;
import de.algorythm.cms.expath.model.ExpathPackage;

/**
 * Supports read and write access for expath package descriptors.
 * Can also derive expath package components from multiple directories.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public class ExpathPackageManager {

	/**
	 * Writes a generated expath package descriptor.
	 * @param args command line args
	 * @throws Exception on error
	 */
	static public void main(final String[] args) throws Exception {
		if (args.length != 6) {
			System.err.println("Usage: expath-pkg-generator EXPATH_PKG_FILE NAME ABBREV TITLE VERSION SOURCE_DIRECTORY");
			System.exit(1);
			return;
		}

		final ExpathPackage pkg = new ExpathPackage(URI.create(args[1]),
				args[2], args[3], args[4]);
		final ExpathPackageManager manager = new ExpathPackageManager();
		final Path sourceDirectory = Paths.get(args[5]);
		final Path targetDescriptorFile = Paths.get(args[0]);

		if (Files.exists(targetDescriptorFile)) {
			System.err.println("Target descriptor file " + targetDescriptorFile + " already exists");
			System.exit(2);
			return;
		}

		manager.deriveComponents(pkg, StandardCharsets.UTF_8, sourceDirectory);
		manager.saveDescriptor(pkg, targetDescriptorFile);
	}

	private final JAXBContext jaxbContext;
	private final List<String> filter = new LinkedList<>();
	private final Map<String, IResourceHandler> handlerMap = new HashMap<>();
	private final IResourceHandler defaultHandler;

	public ExpathPackageManager() {
		try {
			jaxbContext = JAXBContext.newInstance(ExpathPackage.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		
		defaultHandler = new ResourceHandler();
		IResourceHandler xqueryHandler = new XQueryModuleHandler();
		IResourceHandler xsltHandler = new XsltModuleHandler();

		filter.add("*");
		handlerMap.put("xqm", xqueryHandler);
		handlerMap.put("xq", xqueryHandler);
		handlerMap.put("xsl", xsltHandler);
	}

	/**
	 * Reads the expath package descriptor from a given input stream.
	 * @param in expath package descriptor input stream
	 * @return expath package descriptor object
	 * @throws JAXBException if descriptor could not be read
	 */
	public ExpathPackage readDescriptor(InputStream in) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final StreamSource source = new StreamSource(in);
		
		return unmarshaller.unmarshal(source, ExpathPackage.class).getValue();
	}

	/**
	 * Derives expath component descriptions from the given sourceDirectories
	 * and adds them to the expath package descriptor.
	 * @param pkg expath package descriptor
	 * @param encoding component file encoding
	 * @param sourceDirectories component source directories
	 * @throws IOException
	 */
	public void deriveComponents(final ExpathPackage pkg, final Charset encoding,
			final Path... sourceDirectories) throws IOException {
		for (Path sourceDirectory : sourceDirectories) {
			if (!Files.exists(sourceDirectory))
				throw new IllegalStateException("Given source directory "
						+ sourceDirectory + " does not exist");

			if (!Files.isDirectory(sourceDirectory))
				throw new IllegalStateException("Given source directory "
						+ sourceDirectory + " is not a directory");
		}

		for (Path sourceDirectory : sourceDirectories) {
			final URI rootDirectory = sourceDirectory.toUri().normalize();

			Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					handleResource(pkg, file.toUri(), rootDirectory, encoding);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	/**
	 * Creates a XAR archive containing the expath package descriptor
	 * and all overlayed files of the given source directories.
	 * @param targetXarFile target XAR file path
	 * @param encoding target XAR file encoding
	 * @param pkg expath package descriptor
	 * @param sourceDirectories source directories to include sources from
	 * @throws IOException if file write failed
	 * @throws JAXBException if descriptor marshalling failed
	 */
	public void createXarArchive(Path targetXarFile, Charset encoding, ExpathPackage pkg,
			Path... sourceDirectories) throws IOException, JAXBException {
		validateUniqueNames(pkg);
		
		try (OutputStream out = Files.newOutputStream(targetXarFile)) {
			final ZipOutputStream zip = new ZipOutputStream(out, encoding);

			try {
				// Package descriptor
				final Marshaller marshaller = jaxbContext.createMarshaller();
				final ZipEntry pkgEntry = new ZipEntry("expath-pkg.xml");
				final String prefix = pkg.getAbbrev() + '/';

				zip.putNextEntry(pkgEntry);
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.marshal(pkg, zip);
				zip.closeEntry();

				// Package components
				for (AbstractComponent component : pkg.getComponents()) {
					final String relPath = component.getFile().getPath();
					final Path sourceFile = resolveFile(relPath, sourceDirectories);
					final ZipEntry entry = new ZipEntry(prefix + component.getFile());

					zip.putNextEntry(entry);
					Files.copy(sourceFile, zip);
					zip.closeEntry();
				}
			} finally {
				zip.close();
			}
		}
	}
	
	private void validateUniqueNames(ExpathPackage pkg) {
		final Set<String> names = new HashSet<>();
		final Set<String> files = new HashSet<>();
		
		for (AbstractComponent comp : pkg.getComponents()) {
			if (!names.add(comp.getName()))
				throw new IllegalArgumentException("Duplicate component name '" + comp.getName() + '\'');
			
			if (!files.add(comp.getFile().toASCIIString()))
				throw new IllegalArgumentException("Duplicate component file '" + comp.getName() + '\'');
		}
	}

	void saveDescriptor(ExpathPackage pkg, Path toFile) throws JAXBException,
			IOException {
		final OutputStream out = Files.newOutputStream(toFile);
		final Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(pkg, out);
	}

	private void handleResource(ExpathPackage pkg, URI fileUri,
			URI rootDirectory, Charset encoding) {
		if (isIncluded(fileUri)) {
			final String extension = FilenameUtils.getExtension(fileUri
					.getPath());
			final IResourceHandler handler = handlerMap.get(extension);

			try {
				if (handler == null) { // Execute default handler
					defaultHandler.registerResource(pkg, fileUri, rootDirectory.relativize(fileUri), encoding);
				} else { // Execute registered extension handler
					handler.registerResource(pkg, fileUri, rootDirectory.relativize(fileUri), encoding);
				}
			} catch (IOException e) {
				throw new RuntimeException(extension + " resource error: "
						+ e.getMessage(), e);
			}
		}
	}

	private Path resolveFile(String filePath, Path... sourceDirectories)
			throws IOException {
		if (filePath.startsWith("/"))
			throw new IllegalStateException("Absolute path is not allowed: " + filePath);

		for (Path sourceDirectory : sourceDirectories) {
			final Path sourceFile = Paths.get(sourceDirectory.toUri().resolve(filePath));

			if (Files.exists(sourceFile))
				return sourceFile;
		}

		throw new FileNotFoundException("Cannot find " + filePath
				+ " in source directories " + sourceDirectories);
	}

	private boolean isIncluded(URI resourceUri) {
		for (String pattern : filter) {
			if (wildCardMatch(resourceUri.getPath(), pattern))
				return true;
		}

		return false;
	}

	private boolean wildCardMatch(String text, String pattern) {
		final String[] cards = pattern.split("\\*");
		int startIdx = 0;

		// TODO: case no star at pattern's beginning or end

		for (String card : cards) {
			int idx = text.indexOf(card, startIdx);

			if (idx == -1)
				return false;

			startIdx = idx + card.length();
		}

		return true;
	}
}
