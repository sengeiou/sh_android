package com.fav24.shootr.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BaseDTO {
	protected String service;
	protected Status status;
	protected String jsonAttributes;

	public BaseDTO(){ super(); }
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getJsonAttributes() {
		return jsonAttributes;
	}

	public void setJsonAttributes(String jsonAttributes) {
		this.jsonAttributes = jsonAttributes;
	}

	@Override
	public String toString() {
		return "BaseDTO [service=" + service + ", status=" + status + ", jsonAttributes=" + jsonAttributes + "]";
	}
}
