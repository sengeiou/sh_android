package com.fav24.shootr.dao.domain;

public class Notification {
	private String token;
	private Long idPushEngine;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getIdPushEngine() {
		return idPushEngine;
	}

	public void setIdPushEngine(Long idPushEngine) {
		this.idPushEngine = idPushEngine;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[pe=").append(idPushEngine).append(", t=").append(token).append("]");
		return builder.toString();
	}
}
