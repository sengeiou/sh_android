package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.SeasonDAO;
import com.fav24.shootr.dao.domain.Season;

@Component
public class OptaSeasonProcessor implements ItemProcessor<OptaSeasonItem, OptaSeasonItem> {

	@Autowired
	private SeasonDAO seasonDAO;
	
	@Autowired
	private CompetitionDAO competitionDAO;

	@Autowired
	private OptaSeasonMatcher loadOptaSeasonMatcher;

	@Override
	public OptaSeasonItem process(OptaSeasonItem item) throws Exception {
		item.setCompetition(competitionDAO.getCompetitionByOptaId(item.getIdCompetitionOpta()));
		if(item.getCompetition() == null) { return null; }
		
		//recuperamos las seasons de bbdd
		List<Season> domainSeasons = seasonDAO.getAllSeasonsByCompetitionId(item.getCompetition().getIdCompetition());
		
		//hacemos el match de lo que no esta en bbdd
		loadOptaSeasonMatcher.match(item.getDto(), domainSeasons, item);
		
		return item;
	}
}







