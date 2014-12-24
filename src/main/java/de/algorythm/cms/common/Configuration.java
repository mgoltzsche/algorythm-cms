package de.algorythm.cms.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.inject.Singleton;

@Singleton
public class Configuration {

	static private InputStream getPropertiesStream() {
		final InputStream stream = Configuration.class.getResourceAsStream("/algorythm-cms.properties");
		
		if (stream == null)
			throw new IllegalStateException("Cannot read algorythm-cms.properties");
		
		return stream;
	}
	
	public final Path outputDirectory;
	
	public Configuration() {
		this(getPropertiesStream());
	}
	
	public Configuration(final InputStream propertiesStream) {
		final Properties properties = new Properties();
		
		try {
			properties.load(propertiesStream);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read properties", e);
		}
		
		final String outputDirectoryStr = properties.getProperty("output.directory");
		
		if (outputDirectoryStr == null)
			throw new IllegalStateException("outputDirectory property is not configured");
		
		outputDirectory = Paths.get(outputDirectoryStr);
		
		if (Files.isRegularFile(outputDirectory))
			throw new IllegalStateException("outputDirectory " + outputDirectory + " is an existing file");
	}
}