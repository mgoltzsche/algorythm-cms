package de.algorythm.maven.webResourcesPlugin;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Mojo to generate an expath package descriptor (expath-pgk.xml) and
 * write it with its components into a XAR archive.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 * @version $Id$
 */
@Mojo(name = "minify-js", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
public class JavaScriptMinifierMojo extends AbstractMojo {

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

	/**
	 * JavaScript source files to minify.
	 * Paths should be relative to its source/resource directory.
	 */
	@Parameter(property = "webresources.js.sources", alias = "jsSources")
	private List<String> jsSources;

	/**
	 * Merged JavaScript output file.
	 */
	@Parameter(property = "webresources.js.outputFile", alias = "jsOutputFile")
	private String jsOutputFile;

	/**
	 * Indicates whether JavaScripts should be minified or not.
	 */
	@Parameter(property = "webresources.js.compress", alias = "compressJs")
	private boolean compress = true;

	@Parameter(property = "webresources.js.munge", alias = "mungeJs")
	private boolean munge = true;

	@Parameter(property = "webresources.js.munge", alias = "verboseJs")
	private boolean verbose = false;

	@Parameter(property = "webresources.js.preserveAllSemiColons", alias = "preserveSemiColonsJs")
	private boolean preserveAllSemiColons = false;

	@Parameter(property = "webresources.js.disableOptimizations", alias = "disableOptimizationsJs")
	private boolean disableOptimizations = false;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		final StringBuilder scriptBuilder = new StringBuilder();
		final Charset charset = Charset.forName(encoding);
		final Path buildDir = Paths.get(buildDirectory).resolve("classes");
		final String outputFileName = jsOutputFile == null
				? project.getArtifactId() + '-' +  project.getVersion() + ".min.js"
				: jsOutputFile;
		final Path outputFile = buildDir.resolve(outputFileName);
		
		if (Files.exists(outputFile))
			getLog().warn("JS minification output file " + outputFile + " already exists and will be overwritten");
		
		for (String jsFile : jsSources) {
			final Path jsFilePath = buildDir.resolve(jsFile);
			
			if (!Files.exists(jsFilePath))
				throw new MojoExecutionException(jsFilePath + " does not exist");
			
			String script;
			
			try {
				final byte[] bytes = Files.readAllBytes(jsFilePath);
				script = new String(bytes, charset);
			} catch(IOException e) {
				throw new MojoExecutionException("Cannot read " + jsFilePath + ". " + e.getMessage());
			}
			
			if (compress) {
				final Reader reader = new StringReader(script);
				final JavaScriptErrorReporter reporter = new JavaScriptErrorReporter();
				final Writer writer = new StringWriter();
				
				try {
					final JavaScriptCompressor compressor = new JavaScriptCompressor(reader, reporter);
					
					compressor.compress(writer, 0, munge, verbose, preserveAllSemiColons, disableOptimizations);
				} catch(IOException e) {
					throw new MojoExecutionException("JavaScript minification of " + jsFilePath + " failed. " + e.getMessage(), e);
				}
				
				for (String warning : reporter.warnings)
					getLog().warn(warning);
				
				script = writer.toString();
			}
			
			scriptBuilder.append(script).append("\n");
		}
		
		final String scripts = compress
				? scriptBuilder.toString().replace("\n", "")
				: scriptBuilder.toString();
		
		try {
			Files.write(outputFile, scripts.getBytes(charset));
		} catch(Exception e) {
			throw new MojoExecutionException("Cannot write minified JavaScript to " + outputFile + ". " + e.getMessage(), e);
		}
	}
}
