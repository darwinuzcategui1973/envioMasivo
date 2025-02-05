package com.focus.qwds.ondemand.server.usuario.excepciones;

import java.io.Serializable;
import java.rmi.Remote;

public class LoginInvalidoException extends Exception implements Remote,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546125355179636558L;

	public LoginInvalidoException() {
		super();
	}

	public LoginInvalidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginInvalidoException(String message) {
		super(message);
	}

	public LoginInvalidoException(Throwable cause) {
		super(cause);
	}

	
}
