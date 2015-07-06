package de.algorythm.cms.expath;

import de.algorythm.cms.expath.handler.ResourceHandler;
import de.algorythm.cms.expath.handler.XQueryModuleHandler;
import de.algorythm.cms.expath.handler.XsltModuleHandler;
import de.algorythm.cms.expath.model.AbstractComponent;
import de.algorythm.cms.expath.model.ExpathPackage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * See http://expath.org/spec/pkg#expath-pkg.xsd for XMLSchema
 * Created by max on 30.05.15.
 */
public class ExpathPackageManager {

    static public void main(final String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println("Usage: expath-pkg-generator EXPATH_PKG_FILE NAME ABBREV TITLE VERSION SOURCE_DIRECTORY");
            System.exit(1);
            return;
        }

        final ExpathPackage pkg = new ExpathPackage(URI.create(args[1]), args[2], args[3], args[4]);
        final ExpathPackageManager manager = new ExpathPackageManager();
        final Path sourceDirectory = Paths.get(args[5]);
        final Path targetDescriptorFile = Paths.get(args[0]);

        if (Files.exists(targetDescriptorFile)) {
            System.err.println("Target descriptor file " + targetDescriptorFile + " already exists");
            System.exit(2);
            return;
        }

        manager.deriveComponents(pkg, sourceDirectory);
        manager.saveDescriptor(pkg, targetDescriptorFile);
    }

    private final JAXBContext jaxbContext;
    private final List<String> filter = new LinkedList<>();
    private final Map<String, IResourceHandler> handlerMap = new HashMap<>();
    private final IResourceHandler defaultHandler;

    public ExpathPackageManager() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(ExpathPackage.class);
        defaultHandler = new ResourceHandler();
        IResourceHandler xqueryHandler = new XQueryModuleHandler();
        IResourceHandler xsltHandler = new XsltModuleHandler();

        filter.add("*");
        handlerMap.put("xqm", xqueryHandler);
        handlerMap.put("xq", xqueryHandler);
        handlerMap.put("xsl", xsltHandler);
    }

    public void deriveComponents(final ExpathPackage pkg, Path... sourceDirectories) throws IOException {
        for (Path sourceDirectory : sourceDirectories) {
            if (!Files.exists(sourceDirectory))
                throw new IllegalStateException("Given source directory " + sourceDirectory + " does not exist");

            if (!Files.isDirectory(sourceDirectory))
                throw new IllegalStateException("Given source directory " + sourceDirectory + " is not a directory");
        }

        for (Path sourceDirectory : sourceDirectories) {
            final URI rootDirectory = sourceDirectory.toUri().normalize();

            Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    handleResource(pkg, file.toUri(), rootDirectory);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public void createXarArchive(Path targetXarFile, ExpathPackage pkg, Path... sourceDirectories) throws IOException, JAXBException {
        try (OutputStream out = Files.newOutputStream(targetXarFile)) {
            final ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8);

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
                    final Path sourceFile = resolveFile(component.getFile().getPath(), sourceDirectories);
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

    void saveDescriptor(ExpathPackage pkg, Path toFile) throws JAXBException, IOException {
        final OutputStream out = Files.newOutputStream(toFile);
        final Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(pkg, out);
    }

    private void handleResource(ExpathPackage pkg, URI fileUri, URI rootDirectory) {
        if (isIncluded(fileUri)) {
            final String extension = FilenameUtils.getExtension(fileUri.getPath());
            final IResourceHandler handler = handlerMap.get(extension);

            try {
                if (handler == null) { // Execute default handler
                    defaultHandler.registerResource(pkg, fileUri, rootDirectory.relativize(fileUri));
                } else { // Execute registered extension handler
                    handler.registerResource(pkg, fileUri, rootDirectory.relativize(fileUri));
                }
            } catch (IOException e) {
                throw new RuntimeException(extension + " resource error: " + e.getMessage(), e);
            }
        }
    }

    private Path resolveFile(String filePath, Path... sourceDirectories) throws IOException {
        if (filePath.startsWith("/"))
            throw new IllegalStateException("Absolute path is not allowed: " + filePath);

        for (Path sourceDirectory : sourceDirectories) {
            final Path sourceFile = Paths.get(sourceDirectory.toUri().resolve(filePath));

            if (Files.exists(sourceFile))
                return sourceFile;
        }

        throw new FileNotFoundException("Cannot find " + filePath + " in source directories " + sourceDirectories);
    }

    private boolean isIncluded(URI resourceUri) {
        for (String pattern : filter) {
            if (wildCardMatch(resourceUri.getPath(), pattern))
                return true;
        }

        return false;
    }

    private boolean wildCardMatch(String text, String pattern) {
        final String [] cards = pattern.split("\\*");
        int startIdx = 0;

        // TODO: case no star at pattern's beginning or end

        for (String card : cards) {
            int idx = text.indexOf(card, startIdx);

            if(idx == -1)
                return false;

            startIdx = idx + card.length();
        }

        return true;
    }
}
