package de.algorythm.maven.xarPlugin;

import static org.apache.commons.lang.StringEscapeUtils.escapeCsv;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.basex.api.client.ClientSession;

/**
 * Mojo to deploy an expath XAR package on a basex server.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-09, BSD License
 * @version $Id$
 */
@Mojo(name = "deploy-basex", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
@Execute(goal = "deploy-basex", phase = LifecyclePhase.INSTALL)
public class XarBasexDeployerMojo extends AbstractMojo {

	/**
	 * The Maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	/**
	 * File encoding.
	 */
	@Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8", alias = "encoding")
	private String encoding;

	/**
	 * Location of the source directory.
	 */
	@Parameter(property = "project.build.directory", alias = "buildDirectory")
	private String buildDirectory;
	
	@Parameter(property = "basex.host", defaultValue = "localhost")
	private String host;
	
	@Parameter(property = "basex.port", defaultValue = "1984")
	private int port;
	
	@Parameter(property = "basex.user", defaultValue = "admin")
	private String user;
	
	@Parameter(property = "basex.password", defaultValue = "admin")
	private String password;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		final String finalName = project.getBuild().getFinalName();
		final String xarName = finalName == null
				? project.getArtifactId() + '-' + project.getVersion() : finalName;
		final Path xarFile = Paths.get(buildDirectory).resolve(xarName + ".xar");
		
		try (ClientSession session = new ClientSession(host, port, user, password, System.out)) {
			session.execute("REPO INSTALL " + escapeCsv(xarFile.toString()));
			
			String info = session.info();
			if (info.endsWith("\n"))
				info = info.substring(0, info.length() - "\n".length());
			
			getLog().info(info);
		} catch(Throwable e) {
			throw new MojoExecutionException("BaseX XAR deployment failed. " + e.getMessage() +
					"\n\tbasex.host = " + host +
					"\n\tbasex.port = " + port +
					"\n\tbasex.user = " + user +
					"\n\tbasex.password = ***", e);
		}
	}
}
