package com.fav24.shootr.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	private String errorCode;
	private String message;
	private String explanation;

	public Status(){ super(); }
	
	public Status(String errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public Status(String errorCode, String message, String explanation) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.explanation = explanation;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "Status [errorCode=" + errorCode + ", message=" + message + ", explanation=" + explanation + "]";
	}
}
