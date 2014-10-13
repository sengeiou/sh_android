package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.domain.Competition;

@Component
public class OptaCompetitionProcessor implements ItemProcessor<CompetitionDTO, Competition> {

	@Autowired
	private CompetitionDAO competitionDAO;

	@Autowired
	private DTOtoDomainTransformer dTOtoDomainTransformer;

	@Autowired
	private OptaDataShared loadOptaDataShared;

	@Override
	public Competition process(CompetitionDTO competitionDTO) throws Exception {
		// cargamos los equipos existentes del area que tratamos
		Competition domainCompetition = competitionDAO.getCompetitionByOptaId(competitionDTO.getCompetition_id());
		boolean persist = false;
		
		if (domainCompetition == null) {
			persist = true;
			// guardamos el id del area del dominio shootr
			Area area = loadOptaDataShared.getAreas().get(competitionDTO.getArea_id());
			competitionDTO.setArea_id(area.getIdArea());
			domainCompetition = dTOtoDomainTransformer.competitionDTOtoDomain(competitionDTO);
		} else {
			
		}

		//guardamos las seasons en el shared para insertarlas en el siguiente paso.
		loadOptaDataShared.getSeasonsByCompetitionId().put(domainCompetition.getIdCompetitionOpta(), competitionDTO.getSeasons());
		loadOptaDataShared.setCompetitionIdOptaQueue(domainCompetition.getIdCompetitionOpta());
		
		return (persist) ? domainCompetition : null;
	}
}
