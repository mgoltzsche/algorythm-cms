package de.algorythm.cms.common.util;

import java.io.File;

public class FilePathUtil {

	static public String toSystemSpecificPath(final String path) {
		return path.replaceAll("/", File.separator);
	}
}
