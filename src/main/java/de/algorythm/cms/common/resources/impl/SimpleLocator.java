package de.algorythm.cms.common.resources.impl;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Locator;

public class SimpleLocator implements Locator {

	static public final SimpleLocator INSTANCE = new SimpleLocator();
	
	@Override
	public String getPublicId() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getSystemId() {
		return StringUtils.EMPTY;
	}

	@Override
	public int getLineNumber() {
		return 0;
	}

	@Override
	public int getColumnNumber() {
		return 0;
	}
}
