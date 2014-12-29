package de.algorythm.cms.common.resources.meta;

public class MetadataExtractionException extends Exception {

	static private final long serialVersionUID = 4289623430907423698L;

	public MetadataExtractionException(final String message, final Exception e) {
		super(message, e);
	}
	
	public MetadataExtractionException(final String message) {
		super(message);
	}
}
