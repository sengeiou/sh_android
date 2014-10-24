package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import org.springframework.stereotype.Service;

import com.fav24.shootr.batch.rest.parser.Requestor;

@Service
public class TeamRequestorStrategy extends ParentTeamRequestorStrategy {

    @Override
    public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
        long idTeam = id[0];
        return urlBase + "/soccer/get_teams?type=team&detailed=yes&id=" + idTeam + getAuthUrl();
    }
}
