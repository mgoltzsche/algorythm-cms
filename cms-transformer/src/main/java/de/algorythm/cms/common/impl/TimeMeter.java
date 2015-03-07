package de.algorythm.cms.common.impl;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeMeter {

	static private final Logger log = LoggerFactory.getLogger(TimeMeter.class);
	
	static public TimeMeter meter(final String name) {
		return new TimeMeter(name);
	}
	
	private final String name;
	private final long startMillis;
	private long millisElapsed;

	private TimeMeter(final String name) {
		this.name = name;
		this.startMillis = System.currentTimeMillis();
	}

	public void finish() {
		millisElapsed = System.currentTimeMillis() - startMillis;
		final double secondsElapsed = millisElapsed / 1000.0;
		
		log.info(String.format(Locale.ENGLISH, "%-45s took %.3f seconds", name, secondsElapsed));
	}
}
