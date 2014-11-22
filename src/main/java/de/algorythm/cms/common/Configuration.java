package de.algorythm.cms.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Singleton;

@Singleton
public class Configuration {

	static private final LocaleResolver localeResolver = new LocaleResolver();
	static private InputStream getPropertiesStream() {
		final InputStream stream = Configuration.class.getResourceAsStream("/algorythm-cms.properties");
		
		if (stream == null)
			throw new IllegalStateException("Cannot read algorythm-cms.properties");
		
		return stream;
	}
	
	public final Locale defaultLanguage;
	public final File outputDirectory;
	
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
		
		final String defaultLangStr = properties.getProperty("defaultLanguage");
		final String outputDirectoryStr = properties.getProperty("output.directory");
		
		if (outputDirectoryStr == null)
			throw new IllegalStateException("outputDirectory property is not configured");
		
		outputDirectory = new File(outputDirectoryStr);
		
		if (outputDirectory.isFile())
			throw new IllegalStateException("outputDirectory " + outputDirectory + " is an existing file");
		
		defaultLanguage = defaultLangStr == null
				? Locale.ENGLISH
				: localeResolver.getLocale(defaultLangStr);
	}
}