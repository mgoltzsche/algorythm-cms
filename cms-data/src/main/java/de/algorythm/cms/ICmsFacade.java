package de.algorythm.cms;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

import org.basex.core.Command;

public interface ICmsFacade extends Closeable {

	void openOrCreateDatabase(String name) throws IOException;
	void openOrCreateDatabase(String name, Path source) throws IOException;
	void execute(Command command, IHandler<String> handler);
	void execute(String path);
}
