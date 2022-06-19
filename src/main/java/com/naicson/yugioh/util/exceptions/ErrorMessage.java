package com.naicson.yugioh.util.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorMessage extends RuntimeException {
	
	private static final long serialVersionUID = 1149241039409861914L;
	
	private String status_code;
	private String msg;
	private HttpStatus httpStatus;
	
	public ErrorMessage(HttpStatus httStatus, String msg) {
		super();
		this.setHttpStatus(httStatus);
		this.msg = msg;
	}
	
	public ErrorMessage(String status_code, String msg) {
		super();
		this.status_code = status_code;
		this.msg = msg;
	}

	public ErrorMessage(String msg) {
		super(msg);
	}
	
	public ErrorMessage(String msg, Throwable cause) {
		super(msg, cause);
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	
	
}
