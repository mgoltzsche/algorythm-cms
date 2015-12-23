package de.algorythm.maven.webResourcesPlugin;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
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
import org.w3c.css.sac.InputSource;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * Mojo to generate an expath package descriptor (expath-pgk.xml) and
 * write it with its components into a XAR archive.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 * @version $Id$
 */
@Mojo(name = "compile-scss", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
public class ScssCompilerMojo extends AbstractMojo {

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
	 * SCSS source files to compile.
	 * Paths should be relative to its source/resource directory.
	 */
	@Parameter(property = "webresources.scss.sources", alias = "scssSources")
	private List<String> scssSources;

	/**
	 * Indicates whether the SCSS compilation output should be compiled or not.
	 */
	@Parameter(property = "webresources.scss.compress.ouput", alias = "compressScssOutput")
	private boolean compress = true;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException {
		final Charset charset = Charset.forName(encoding);
		final Path buildDir = Paths.get(buildDirectory).resolve("classes");
		
		for (String scssFile : scssSources) {
			if (!scssFile.endsWith(".scss"))
				throw new MojoExecutionException("SCSS source files must have extension scss");
			
			final Path absoluteScssFile = buildDir.resolve(scssFile);
			final String outputFileName = scssFile.replaceAll("\\.scss$", ".css");
			final Path outputFile = buildDir.resolve(outputFileName);
			
			if (!Files.exists(absoluteScssFile))
				throw new MojoExecutionException(absoluteScssFile + " does not exist");
			
			if (Files.exists(outputFile))
				getLog().warn("SCSS compilation output file " + outputFile + " already exists and will be overwritten");
			
			try {
				final String css = compileScss(absoluteScssFile.toString());
				
				Files.write(outputFile, css.getBytes(charset));
			} catch(Exception e) {
				throw new MojoExecutionException("SCSS compilation failed. " + e.getMessage(), e);
			}
		}
	}

	private String compileScss(final String scssFile) throws Exception {
		final SCSSDocumentHandler docHandler = new SCSSDocumentHandlerImpl();
		final SCSSErrorHandler errorHandler = new SCSSErrorHandler();
		final ScssStylesheet stylesheet = docHandler.getStyleSheet();
		final Parser parser = new Parser();
		final InputSource source = new InputSource(scssFile);
		
		parser.setErrorHandler(errorHandler);
		parser.setDocumentHandler(docHandler);
		parser.parseStyleSheet(source);
		stylesheet.addResolver(new ScssResolver(encoding));
		stylesheet.setCharset(encoding);
        stylesheet.setFile(new File(scssFile));
		stylesheet.compile();
		final String css = stylesheet.printState();
		
		if (compress) {
			final StringReader cssReader = new StringReader(css);
			final CssCompressor compressor = new CssCompressor(cssReader);
			final StringWriter writer = new StringWriter();
			
			compressor.compress(writer, 0);
			
			return writer.toString().replace("\n", "");
		}
		
		return css;
	}
}
