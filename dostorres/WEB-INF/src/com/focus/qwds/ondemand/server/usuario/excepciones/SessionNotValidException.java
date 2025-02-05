package com.focus.qwds.ondemand.server.usuario.excepciones;

import java.io.Serializable;
import java.rmi.Remote;

public class SessionNotValidException extends Exception implements Remote,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546125355179636558L;

	public SessionNotValidException() {
		super();
	}

	public SessionNotValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionNotValidException(String message) {
		super(message);
	}

	public SessionNotValidException(Throwable cause) {
		super(cause);
	}

	
}
