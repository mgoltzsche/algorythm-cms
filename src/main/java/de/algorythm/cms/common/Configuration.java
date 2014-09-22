package de.algorythm.cms.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Singleton;

import de.algorythm.cms.common.util.FilePathUtil;

@Singleton
public class Configuration {

	static private final LocaleResolver localeResolver = new LocaleResolver();
	static private InputStream getPropertiesStream() {
		final InputStream stream = Configuration.class.getResourceAsStream("/algorythm-cms.properties");
		
		if (stream == null)
			throw new IllegalStateException("Cannot read algorythm-cms.properties");
		
		return stream;
	}
	
	public final File repository;
	public final Locale defaultLanguage;
	
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
		
		final String repositoryStr = properties.getProperty("repository");
		final String defaultLangStr = properties.getProperty("defaultLanguage");
		
		repository = new File(repositoryStr == null
				? System.getProperty("user.home") + File.separator + "algorythm-cms"
				: FilePathUtil.toSystemSpecificPath(repositoryStr));
		
		if (repository.exists()) {
			if (!repository.isDirectory())
				throw new IllegalStateException("Given repository "
						+ repository.getAbsolutePath() + " is not a directory");
		} else {
			repository.mkdirs();
		}
		
		defaultLanguage = defaultLangStr == null
				? Locale.ENGLISH
				: localeResolver.getLocale(defaultLangStr);
	}
}