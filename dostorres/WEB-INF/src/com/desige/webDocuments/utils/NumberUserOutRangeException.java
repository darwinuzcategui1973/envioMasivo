package com.desige.webDocuments.utils;

/**
 * The Class ArgumentException.
 */
public class NumberUserOutRangeException extends Exception {

	private static final long serialVersionUID = -7032794941612915245L;

	/**
	 * Instantiates a new argument exception.
	 */
	public NumberUserOutRangeException() {
		super("Los Usuarios exceden en numero para la version lite");
	}

	/**
	 * Instantiates a new argument exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public NumberUserOutRangeException(final String arg0) {
		super(arg0);
		
	}

	/**
	 * ServiceXmlDeserializationException Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param innerException
	 *            the inner exception
	 */
	public NumberUserOutRangeException(String message, Exception innerException) {
		super(message, innerException);
	}
	
	/**
	 * Initializes a new instance of the System.
	 * ArgumentException class with a specified
     *     error message and the name of the 
     *     parameter that causes this exception.
	 * 
	 * @param message
	 *            The error message that explains the reason for the exception.
	 * @param paramName
	 *            The name of the parameter that caused the current exception.
	 */
	public NumberUserOutRangeException(String message, String paramName) {
		super(message+" Parameter that caused " +
				"the current exception :"+paramName);
	}

}
