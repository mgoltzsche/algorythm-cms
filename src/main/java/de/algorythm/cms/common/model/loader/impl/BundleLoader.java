package de.algorythm.cms.common.model.loader.impl;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.impl.xml.contentHandler.PageInfoHandler;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.entity.impl.SupportedLocale;
import de.algorythm.cms.common.model.loader.IBundleLoader;

@Singleton
public class BundleLoader implements IBundleLoader {

	private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private final JAXBContext jaxbContext;
	
	@Inject
	public BundleLoader(final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
		parserFactory.setNamespaceAware(true);
	}
	
	@Override
	public IBundle getBundle(final Path bundleFile) throws JAXBException {
		if (!Files.exists(bundleFile))
			throw new IllegalArgumentException(bundleFile + " does not exist");
		
		if (!Files.exists(bundleFile))
			throw new IllegalArgumentException(bundleFile + " is a directory");
		
		final Bundle bundle = readBundle(bundleFile);
		
		bundle.setLocation(bundleFile.getParent());
		
		if (bundle.getTitle() == null)
			bundle.setTitle(bundle.getName());
		
		if (bundle.getDefaultLocale() == null)
			bundle.setDefaultLocale(Locale.ENGLISH);
		
		if (bundle.getContextPath() == null)
			bundle.setContextPath("");
		
		final Set<ISupportedLocale> supportedLocales = bundle.getSupportedLocales();
		final Set<ISupportedLocale> mergedSupportedLocales = new LinkedHashSet<ISupportedLocale>(supportedLocales.size() + 1);
		
		mergedSupportedLocales.add(new SupportedLocale(bundle.getDefaultLocale()));
		mergedSupportedLocales.addAll(supportedLocales);
		bundle.setSupportedLocales(mergedSupportedLocales);
		
		return bundle;
	}
	
	private Bundle readBundle(final Path siteCfgFile) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Source source = new StreamSource(siteCfgFile.toString());
		
		return unmarshaller.unmarshal(source, Bundle.class).getValue();
	}
	
	@Override
	public IPage loadPages(final IBundle bundle, final Locale locale) {
		final Path rootPath = bundle.getLocation().resolve("international/pages");
		final IPage startPage;
		
		try {
			final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			final PageInfoHandler pageInfoHandler = new PageInfoHandler();
			
			reader.setContentHandler(pageInfoHandler);
			startPage = loadPages(rootPath, "", bundle.getName(), reader, pageInfoHandler);
		} catch(Exception e) {
			throw new RuntimeException("Cannot load start page of " + bundle.getName(), e);
		}
		
		if (startPage == null)
			throw new IllegalStateException("No start page defined in " + rootPath);
		
		return startPage;
	}
	
	static private class Result {
		public IPage page;
	}
	
	private IPage loadPages(final Path location, final String path, final String name, final XMLReader reader, final PageInfoHandler handler) throws IOException, SAXException {
		final Path pageDirectory = location.getFileSystem().getPath(location.toString(), path);
		//final PageInfo page = loadPage(location, path, name, reader, handler);
		final LinkedList<IPage> pageStack = new LinkedList<IPage>();
		final Result result = new Result();
		
		Files.walkFileTree(pageDirectory, new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				final Path pageFile = dir.resolve("page.xml");
				
				if (Files.exists(pageFile)) {
					final IPage parentPage = pageStack.peek();
					final String pageName = parentPage == null
							? name
							: pageFile.getParent().getFileName().toString();
					String publicPath = location.relativize(dir).toString();
					final IPage page;
					
					if (!publicPath.isEmpty())
						publicPath = '/' + publicPath;
					
					try {
						page = loadPage(pageFile, publicPath, pageName, reader, handler);
					} catch (SAXException e) {
						throw new RuntimeException("Cannot load page " + pageFile, e);
					}
					
					if (parentPage != null)
						parentPage.getPages().add(page);
					
					pageStack.push(page);
					
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
				result.page = pageStack.pop();
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		return result.page;
	}
	
	private PageInfo loadPage(final Path pageFile, final String publicPath, final String name, final XMLReader xmlReader, final PageInfoHandler handler) throws IOException, SAXException {
		final PageInfo page = new PageInfo(publicPath, name);
		final Reader fileReader = Files.newBufferedReader(pageFile, StandardCharsets.UTF_8);
		final InputSource source = new InputSource(fileReader);
		
		source.setSystemId(pageFile.toString());
		handler.setPage(page);
		
		try {
			xmlReader.parse(source);
		} catch (InformationCompleteException e) {
		}
		
		return page;
	}
}
