package com.desige.webDocuments.serviceDocument.request;

public class ResponseJson<T> {

	private boolean success = false;
	private int code = 200;
	private String message;
	private T data;

	public ResponseJson() {
	}
	
	public ResponseJson(boolean success, int code, String message, T data) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
