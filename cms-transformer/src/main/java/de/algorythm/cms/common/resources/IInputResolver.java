package de.algorythm.cms.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface IInputResolver {

	static public final IInputResolver DEFAULT = new IInputResolver() {
		@Override
		public IInputSource resolveResource(URI publicUri)
				throws ResourceNotFoundException, IOException {
			throw new ResourceNotFoundException("Cannot resolve " + publicUri);
		}
		
		@Override
		public InputStream createInputStream(URI publicUri)
				throws ResourceNotFoundException, IOException {
			throw new ResourceNotFoundException("Cannot resolve resource URI " + publicUri);
		}
	};
	
	IInputSource resolveResource(URI publicUri) throws ResourceNotFoundException, IOException;
	InputStream createInputStream(URI publicUri) throws ResourceNotFoundException, IOException;
}
