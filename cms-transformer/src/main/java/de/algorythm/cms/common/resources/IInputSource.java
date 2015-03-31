package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface IInputSource {

	String getName();
	Date getCreationTime();
	Date getLastModifiedTime();
	InputStream createInputStream() throws IOException;
}
