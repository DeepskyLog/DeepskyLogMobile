/* ====================================================================
 * /util/SchemaException.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package org.deepskylog.util.util;

import org.deepskylog.util.OALException;


/**
 * The SchemaException indicates problems during loading or
 * parsing of a xml schema file.<br>
 * Typical causes of a SchemaException may be:
 * Malformed XML Documents or invalid schema elements (required value
 * missing).
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
@SuppressWarnings("serial")
public class SchemaException extends OALException {
	
	// ------------
	// Constructors ------------------------------------------------------
	// ------------

	// -------------------------------------------------------------------
	/**
	 * Constructs a new SchemaException.<br>
	 * 
	 * @param message Message which describes the cause of the 
	 * problem.
	 */
	public SchemaException(String message) {
		
		super(message);
		
	}


	// -------------------------------------------------------------------
	/**
	 * Constructs a new SchemaException.<br>
	 * 
	 * @param message Message which describes the cause of the 
	 * problem.
	 * @param cause A exception that was the root cause of this
	 * ConfigException.
	 */
	public SchemaException(String message, Throwable cause) {

		super(message, cause);

	}

}
