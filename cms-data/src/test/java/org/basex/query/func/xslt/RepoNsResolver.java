package org.basex.query.func.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.basex.query.util.pkg.Repo;
import org.basex.util.hash.TokenMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.expath.ExpathPackageManager;
import de.algorythm.cms.expath.model.AbstractComponent;
import de.algorythm.cms.expath.model.ExpathPackage;

public class RepoNsResolver implements URIResolver {

	static private final Logger log = LoggerFactory.getLogger(RepoNsResolver.class);
	
	private final ExpathPackageManager pkgReader = new ExpathPackageManager();
	private final Repo repo;
	private Map<String, Path> resourceMap;
	
	public RepoNsResolver(Repo repo) {
		this.repo = repo;
		invalidate();
	}
	
	public void invalidate() {
		final Map<String, Path> fileMap = new HashMap<>();
		final TokenMap pkgs = repo.pkgDict();
		final String repoPath = repo.path().path() + '/';
		final Iterator<byte[]> iter = pkgs.iterator();
		
		while(iter.hasNext()) {
			final byte[] key = iter.next();
			final String name = new String(key);
			final String dirName = new String(pkgs.get(key));
			final String pkgDir = repoPath + dirName + '/';
			final String pkgFile = pkgDir + "expath-pkg.xml";
			
			try (InputStream in = Files.newInputStream(Paths.get(pkgFile))) {
				final ExpathPackage pkg = pkgReader.readDescriptor(in);
				final String contentDir = pkgDir + pkg.getAbbrev() + '/';
				
				for (AbstractComponent component : pkg.getComponents()) {
					String path = contentDir + component.getFile().toASCIIString();
					Path file = Paths.get(path);
					
					fileMap.put(component.getName(), file);
					System.out.println("NS: " + component.getName() + " -> " + path);
				}
			} catch (IOException e) {
				log.error("Cannot read repo package " + name, e);
			} catch (JAXBException e) {
				log.error("Invalid package descriptor of " + name, e);
			}
		}
		
		this.resourceMap = fileMap;
	}

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		String uri = URI.create(base).resolve(href).normalize().toString();
		Path file = resourceMap.get(uri);
		StreamSource source;
		System.out.println("###################### " + uri + " -> " + file);
		if (file == null) {
			log.error("Cannot resolve " + uri);
			return null;
		}
		
		try {
			source = new StreamSource(Files.newInputStream(file));
		} catch (IOException e) {
			throw new TransformerException("Cannot find resource " + uri + " located in " + file, e);
		}
		
		source.setSystemId(uri);
		
		return source;
	}
}
