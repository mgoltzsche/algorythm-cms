package de.algorythm.cms.common.resources;

public class ResourceNotFoundException extends Exception {

	static private final long serialVersionUID = -7987669542493041921L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
