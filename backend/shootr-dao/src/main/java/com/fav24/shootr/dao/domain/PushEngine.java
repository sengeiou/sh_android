package com.fav24.shootr.dao.domain;

public class PushEngine {
	private Long idPushEngine;
	private String description;
	private Long devicesPerInstance;
	private Long maxInstances;
	
	public Long getIdPushEngine() {
		return idPushEngine;
	}
	public String getDescription() {
		return description;
	}
	public Long getDevicesPerInstance() {
		return devicesPerInstance;
	}
	public Long getMaxInstances() {
		return maxInstances;
	}
	public void setIdPushEngine(Long idPushEngine) {
		this.idPushEngine = idPushEngine;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDevicesPerInstance(Long devicesPerInstance) {
		this.devicesPerInstance = devicesPerInstance;
	}
	public void setMaxInstances(Long maxInstances) {
		this.maxInstances = maxInstances;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PushEngine [idPushEngine=").append(idPushEngine).append(", description=").append(description).append(", maxInstances=").append(maxInstances).append("]");
		return builder.toString();
	}
}
