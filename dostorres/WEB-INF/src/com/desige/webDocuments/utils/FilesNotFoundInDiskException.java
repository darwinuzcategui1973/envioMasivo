package com.desige.webDocuments.utils;

public class FilesNotFoundInDiskException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FilesNotFoundInDiskException() {
		super();
	}

	public FilesNotFoundInDiskException(String message) {
		super(message);
	}

}
