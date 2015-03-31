package de.algorythm.cms.common.resources.meta;

import java.io.IOException;

public class MetadataExtractionException extends IOException {

	static private final long serialVersionUID = 4289623430907423698L;

	public MetadataExtractionException(final String message, final Exception e) {
		super(message, e);
	}

	public MetadataExtractionException(final String message) {
		super(message);
	}

	public MetadataExtractionException(final Exception e) {
		super(e.getClass().getName() + ": " + e);
	}
}
