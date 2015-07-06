package de.algorythm.maven.xarPlugin;

import de.algorythm.cms.expath.ExpathPackageManager;
import de.algorythm.cms.expath.model.ExpathPackage;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.FileUtils;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Mojo to generate an expath-pgk.xml and package it with its components into a XAR archive.
 * 
 * @author <a href="mailto:max.goltzsche@algorythm.de">Max Goltzsche</a>
 * @version $Id$
 */
@Mojo(name = "build-xar", defaultPhase = LifecyclePhase.PACKAGE , requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
@Execute(goal = "build-xar", phase = LifecyclePhase.PACKAGE, lifecycle = "xarcycle")
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
		getLog().info("Execute expath builder\n" + project.getArtifact().getFile());

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
		final URI pkgUri = derivePackageName();
		final String abbrev = project.getArtifactId();
		final String title = project.getName();
		final String version = project.getVersion();
		final String finalName = project.getBuild().getFinalName();
		final String xarName = finalName == null
				? abbrev + '-' + version : finalName;
		final Path xarFile = Paths.get(buildDirectory).resolve(xarName + ".xar");
		final Path sourceDirectory = Paths.get(project.getBuild().getSourceDirectory());
		final ExpathPackage pkg = new ExpathPackage(pkgUri, abbrev, title, version);
		final ExpathPackageManager xarManager = new ExpathPackageManager();

		xarManager.deriveComponents(pkg, sourceDirectory);
		Files.createDirectories(xarFile.getParent());
		xarManager.createXarArchive(xarFile, pkg, sourceDirectory);

		return xarFile;
	}

	private URI derivePackageName() {
		final StringBuilder uri = new StringBuilder("http://");
		final String[] groupIdSegments = project.getGroupId().split("\\.");

		if (groupIdSegments.length < 2)
			throw new IllegalStateException("Invalid group ID " + project.getGroupId() + ". Expecting a qualified name seperated by at least 2 dots.");

		final String topLevel = groupIdSegments[0];
		final String name = groupIdSegments[1];

		uri.append(name).append('.').append(topLevel);

		for (int i = 2; i < groupIdSegments.length; i++)
			uri.append('/').append(groupIdSegments[i]);

		uri.append('/').append(project.getArtifactId());

		return URI.create(uri.toString());
	}
}
