package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.io.OutputStream;

public interface IOutputTarget {

	String getPublicPath();
	OutputStream createOutputStream() throws IOException;
}
