package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import com.fav24.shootr.batch.rest.parser.RequestorStrategy;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseRequestorStrategy implements RequestorStrategy {

	@Value("${webservice.opta.userName}")
	String userName;

	@Value("${webservice.opta.token}")
	String token;

	@Value("${webservice.opta.urlBase}")
	String urlBase;
	
	protected String getAuthUrl(){
		return "&username=" + userName + "&authkey=" + token;
	}
}
