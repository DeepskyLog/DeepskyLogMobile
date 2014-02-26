/* ====================================================================
 * /util/ConfigException.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package org.deepskylog.util.util;

import org.deepskylog.util.OALException;


/**
 * The ConfigException indicates problems during loading or
 * initializing of configuration information.<br>
 * Typical causes of a ConfigException may be:
 * Corrupted JAR file, corrupted JAR file entry, corrupted
 * config file (header information bad, or not given, type
 * or classpath not set...)
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
@SuppressWarnings("serial")
public class ConfigException extends OALException {
	
	// ------------
	// Constructors ------------------------------------------------------
	// ------------

	// -------------------------------------------------------------------
	/**
	 * Constructs a new ConfigException.<br>
	 * 
	 * @param message Message which describes the cause of the 
	 * problem.
	 */
	public ConfigException(String message) {
		
		super(message);
		
	}


	// -------------------------------------------------------------------
	/**
	 * Constructs a new ConfigException.<br>
	 * 
	 * @param message Message which describes the cause of the 
	 * problem.
	 * @param cause A exception that was the root cause of this
	 * ConfigException.
	 */
	public ConfigException(String message, Throwable cause) {

		super(message, cause);

	}

}
