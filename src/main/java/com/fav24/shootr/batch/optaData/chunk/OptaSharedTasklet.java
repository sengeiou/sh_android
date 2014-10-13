package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.batch.rest.parser.RequestorImpl;
import com.fav24.shootr.dao.AreaDAO;
import com.fav24.shootr.dao.domain.Area;

@Component
public class OptaSharedTasklet implements Tasklet {

	@Autowired
	public OptaDataShared optaDataShared;

	@Autowired
	@Qualifier("areaRequestor")
	public Requestor areaRequestor;
	
	@Autowired
	@Qualifier("competitionRequestor")
	public Requestor competitionRequestor;

	@Autowired
	private AreaDAO areaDAO;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		try {
			//recuperamos las areas de opta
			List<AreaDTO> areas = areaRequestor.doRequest(RequestorImpl.LanguageRequest.English);
			// guardamos en la cola del shared bean
			optaDataShared.setAreaQueue(new ArrayBlockingQueue<AreaDTO>(areas.size(), false, areas));
			optaDataShared.setAreaQueueTeams(new ArrayBlockingQueue<AreaDTO>(areas.size(), false, areas));

			//recuperamos las competiciones de opta
			List<CompetitionDTO> competitions = competitionRequestor.doRequest(RequestorImpl.LanguageRequest.English);
			// guardamos en la cola del shared bean
			optaDataShared.setCompetitionQueue(new ArrayBlockingQueue<CompetitionDTO>(competitions.size(), false, competitions));

			// guardamos un mapa <idAreaOpta, Area>
			List<Area> databaseAreas = areaDAO.getAllAreas();
			for (Area area : databaseAreas) {
				optaDataShared.getAreas().put(area.getIdAreaOpta(), area);
			}
			
		} catch (Exception e) {
			// TODO: Gestionar error
		}

		return RepeatStatus.FINISHED;
	}

}
