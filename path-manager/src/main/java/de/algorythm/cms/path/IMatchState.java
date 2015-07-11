package de.algorythm.cms.path;

import java.util.Map;

public interface IMatchState<K, R> {

	/**
	 * Returns the matched key.
	 * @return The matched key. Returns never null
	 */
	K getKey();
	
	/**
	 * Returns the matched resource.
	 * @return The matched resource. Returns never null
	 */
	R getResource();
	
	/**
	 * Returns the whole path to match against.
	 * @return The path
	 */
	String getPath();
	
	/**
	 * Returns the matched path prefix.
	 * @return The matched path prefix. Returns never null
	 */
	String getPrefix();
	
	/**
	 * Returns the matched parameter's names.
	 * @return Array of parameter names. Returns never null
	 */
	String[] getParamNames();
	
	/**
	 * Returns the parameter values.
	 * @return Array of parameter values. Returns never null
	 */
	String[] getParamValues();
	
	/**
	 * Returns the parameters as map.
	 * @return parameter map. Returns never null
	 */
	Map<String, String> getParameters();
}
