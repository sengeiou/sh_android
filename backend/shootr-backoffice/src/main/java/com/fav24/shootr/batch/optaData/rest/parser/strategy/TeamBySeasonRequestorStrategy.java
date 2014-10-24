package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import org.springframework.stereotype.Service;

import com.fav24.shootr.batch.rest.parser.Requestor;

@Service
public class TeamBySeasonRequestorStrategy extends ParentTeamRequestorStrategy {

	@Override
	public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
		long idSeason = id[0];
		//http://api.core.optasports.com/soccer/get_teams?id=9773&type=season&detailed=yes
		return urlBase + "/soccer/get_teams?type=season&id=" + idSeason + "&detailed=yes"+getAuthUrl();
	}
}
