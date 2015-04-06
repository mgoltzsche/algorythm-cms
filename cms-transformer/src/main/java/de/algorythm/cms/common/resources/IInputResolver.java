package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface IInputResolver {

	static public final IInputResolver DEFAULT = new IInputResolver() {
		@Override
		public IInputSource resolveResource(URI publicUri)
				throws IOException {
			return null;
		}
		
		@Override
		public InputStream createInputStream(URI publicUri)
				throws IOException {
			return null;
		}
	};
	
	IInputSource resolveResource(URI publicUri) throws IOException;
	InputStream createInputStream(URI publicUri) throws IOException;
}
