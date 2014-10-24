package com.fav24.shootr.batch.optaMatches.chunk;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.RoundDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.batch.rest.parser.RequestorImpl;
import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.MatchDAO;
import com.fav24.shootr.dao.domain.Match;
import com.fav24.shootr.dao.domain.Season;

@Component
public class OptaMatchProcessor implements ItemProcessor<Season, OptaMatchItem> {

	@Autowired
	private MatchDAO matchDAO;
	
	@Autowired
	private CompetitionDAO competitionDAO;

	@Autowired
	private OptaMatchMatcher loadOptaMatchMatcher;

	@Autowired
	@Qualifier("matchRequestor")
	public Requestor matchRequestor;
	
	@Override
	public OptaMatchItem process(Season season) throws Exception {
		List<RoundDTO> dtos = matchRequestor.doRequest(RequestorImpl.LanguageRequest.Spanish, season.getIdSeasonOpta());
		
		//recuperamos las seasons de bbdd
		List<Match> domainMatches = matchDAO.getAllMatchesBySeasonId(season.getIdSeason());
		
		//hacemos el match de lo que no esta en bbdd o se tiene que actualizar
		return loadOptaMatchMatcher.match(dtos, domainMatches, season.getIdSeason());
	}
}







