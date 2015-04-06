package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.io.OutputStream;

public interface IOutputTarget {

	//String getPublicPath();
	boolean exists();
	OutputStream createOutputStream() throws IOException;
}
