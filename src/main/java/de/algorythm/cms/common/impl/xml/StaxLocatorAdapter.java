package de.algorythm.cms.common.impl.xml;

import javax.xml.stream.Location;

import org.xml.sax.Locator;

public class StaxLocatorAdapter implements Locator {

	private final Location location;
	
	public StaxLocatorAdapter(final Location location) {
		this.location = location;
	}
	
	@Override
	public String getPublicId() {
		return location.getPublicId();
	}

	@Override
	public String getSystemId() {
		return location.getSystemId();
	}

	@Override
	public int getLineNumber() {
		return location.getLineNumber();
	}

	@Override
	public int getColumnNumber() {
		return location.getColumnNumber();
	}
}
