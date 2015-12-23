package de.algorythm.maven.xarPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.algorythm.cms.expath.ExpathPackageManager;
import de.algorythm.cms.expath.model.AbstractComponent;
import de.algorythm.cms.expath.model.ExpathPackage;

/**
 * Mojo to generate an expath package descriptor (expath-pgk.xml) and
 * write it with its components into a XAR archive.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 * @version $Id$
 */
@Mojo(name = "build-xar", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
public class XarBuilderMojo extends AbstractMojo {

	/**
	 * The Maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	/**
	 * File encoding.
	 */
	@Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8", alias = "encoding")
	protected String encoding;

	/**
	 * Location of the source directory.
	 */
	@Parameter(property = "project.build.directory", alias = "buildDirectory")
	protected String buildDirectory;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		try {
			Path xarFilePath = createXarFile();
			File xarFile = new File(xarFilePath.toUri());

			// Register main artifact's file for install and deploy plugins
			project.getArtifact().setFile(xarFile);
		} catch(IOException | JAXBException e) {
			throw new MojoExecutionException("XAR file creation failed: " + e, e);
		}
	}

	private Path createXarFile() throws IOException, JAXBException {
		final Charset charset = Charset.forName(encoding);
		final URI pkgUri = derivePackageName();
		final String abbrev = project.getArtifactId();
		final String title = project.getName();
		final String version = project.getVersion();
		final String finalName = project.getBuild().getFinalName();
		final String xarName = finalName == null
				? abbrev + '-' + version : finalName;
		final Path xarFile = Paths.get(buildDirectory).resolve(xarName + ".xar");
		final Path sourceDir = Paths.get(project.getBuild().getSourceDirectory());
		final Path outputDir = Paths.get(project.getBuild().getOutputDirectory());
		final ExpathPackage pkg = new ExpathPackage(pkgUri, abbrev, title, version);
		final ExpathPackageManager xarManager = new ExpathPackageManager();
		final Path[] sourceDirectories = Files.isDirectory(outputDir)
				? new Path[] {sourceDir, outputDir}
				: new Path[] {sourceDir};
		
		xarManager.deriveComponents(pkg, charset, sourceDirectories);
		Files.createDirectories(xarFile.getParent());
		xarManager.createXarArchive(xarFile, charset, pkg, sourceDirectories);
		
		logPackage(pkg);
		
		return xarFile;
	}
	
	private URI derivePackageName() {
		final StringBuilder uri = new StringBuilder("http://");
		final String[] groupIdSegments = project.getGroupId().split("\\.");
		
		uri.append(groupIdSegments[groupIdSegments.length - 1]);
		
		for (int i = groupIdSegments.length - 2; i >= 0; i--) {
			uri.append('.').append(groupIdSegments[i]);
		}

		uri.append('/').append(project.getArtifactId());

		return URI.create(uri.toString());
	}
	
	private void logPackage(ExpathPackage pkg) {
		final String pkgMsg = "expath package '" + pkg.getName() + '\'';
		if (getLog().isDebugEnabled())
			getLog().debug(pkgMsg + " components:");
		else
			getLog().info(pkgMsg);
		
		if (getLog().isDebugEnabled()) {
			for (String componentType : listComponentTypes(pkg)) {
				getLog().debug(componentType + ": ");
				
				for (AbstractComponent comp : pkg.getComponents()) {
					if (comp.getType().equals(componentType)) {
						final String name = comp.getName();
						final String file = comp.getFile().toString();
						
						getLog().debug(String.format("  %-60s %s", name, file));
					}
				}
			}
		}
	}
	
	private LinkedHashSet<String> listComponentTypes(ExpathPackage pkg) {
		final LinkedHashSet<String> types = new LinkedHashSet<>();
		
		for (AbstractComponent comp : pkg.getComponents())
			types.add(comp.getType());
		
		return types;
	}
}
