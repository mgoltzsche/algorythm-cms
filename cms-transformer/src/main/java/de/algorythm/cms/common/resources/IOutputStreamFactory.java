package de.algorythm.cms.common.resources;

import java.io.OutputStream;

public interface IOutputStreamFactory {

	OutputStream createOutputStream(String publicPath);
}
