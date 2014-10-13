package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import org.springframework.stereotype.Service;

import com.fav24.shootr.batch.rest.parser.Requestor;

@Service
public class TeamByAreaRequestorStrategy extends ParentTeamRequestorStrategy {

	@Override
	public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
		long idArea = id[0];
		return urlBase + "/soccer/get_teams?type=area&id=" + idArea + getAuthUrl();
	}
}
