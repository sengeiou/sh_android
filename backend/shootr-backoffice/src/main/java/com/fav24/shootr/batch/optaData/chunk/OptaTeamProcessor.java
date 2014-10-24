package com.fav24.shootr.batch.optaData.chunk;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.TeamDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.batch.rest.parser.RequestorImpl;
import com.fav24.shootr.dao.SeasonDAO;
import com.fav24.shootr.dao.TeamDAO;
import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.domain.Season;
import com.fav24.shootr.dao.domain.Team;

@Component
public class OptaTeamProcessor implements ItemProcessor<AreaDTO, OptaTeamItem>{
	private static Logger logger = Logger.getLogger(OptaTeamProcessor.class);
	
	@Autowired
	@Qualifier("teamBySeasonRequestor")
	public Requestor teamBySeasonRequestor;
	
	@Autowired
	private OptaDataShared loadOptaDataShared;
	
	@Autowired
	private OptaTeamMatcher loadOptaTeamMatcher;
	
	@Autowired
	private TeamDAO teamDAO;
	
	@Autowired
	private SeasonDAO seasonDAO;
	
	@Override
	public OptaTeamItem process(AreaDTO areaDTO) throws Exception {
		logger.info("Processing "+areaDTO);
		//solo tratamos areas de un pais concreto
		if (areaDTO.getCountrycode() == null || "".equals(areaDTO.getCountrycode())) {return null; }
		
		//miramos si el area existe en bbdd
		Area existingArea = loadOptaDataShared.getAreas().get(areaDTO.getArea_id());
		if(existingArea == null || existingArea.getIdArea() == null){ return null; } 

		OptaTeamItem item = new OptaTeamItem(existingArea);
		
		//cargamos los equipos existentes del area que tratamos
		List<Team> domainTeams = teamDAO.getAllTeamsByAreaId(existingArea.getIdArea());
		
		//los equipos pedidos por season contienen mas detalles (nombres cortos etc)
		List<Season> seasons = seasonDAO.getAllSeasonsByAreaId(existingArea.getIdArea());
		List<TeamDTO> optaTeams = new ArrayList<TeamDTO>();
		if(seasons != null && !seasons.isEmpty()){
			for(Season season: seasons){
				optaTeams.addAll(teamBySeasonRequestor.doRequest(RequestorImpl.LanguageRequest.Spanish, season.getIdSeasonOpta()));
			}
		}
		
		//hacemos match de las operaciones a realizar
		loadOptaTeamMatcher.match(optaTeams, domainTeams, item);
		
		return item;
	}


	
}
