package de.algorythm.cms.common.resources;

import java.io.OutputStream;
import java.net.URI;

public interface IOutputStreamFactory {

	OutputStream createOutputStream(URI publicUri);
}
