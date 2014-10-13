package com.fav24.shootr.batch.optaData.chunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.dao.domain.Area;

@Component
public class OptaDataShared {

	private Map<Long, Area> areas; // <idOpta, domainArea>
	private Map<Long, List<SeasonDTO>> seasonsByCompetitionId; // <idCompetition, seasonDTO>
	private LinkedBlockingQueue<Long> competitionIdOptaQueue;
	private BlockingQueue<AreaDTO> areaQueue;
	private BlockingQueue<AreaDTO> areaQueueTeams;
	private BlockingQueue<CompetitionDTO> competitionQueue;

	public OptaDataShared() {
		areas = new HashMap<Long, Area>();
		seasonsByCompetitionId = new HashMap<Long, List<SeasonDTO>>();
	}

	public Map<Long, Area> getAreas() {
		return areas;
	}

	public void setAreas(Map<Long, Area> areas) {
		this.areas = areas;
	}

	public Map<Long, List<SeasonDTO>> getSeasonsByCompetitionId() {
		return seasonsByCompetitionId;
	}

	public void setAreaQueue(BlockingQueue<AreaDTO> areaQueue) {
		this.areaQueue = areaQueue;
	}

	public void setAreaQueueTeams(BlockingQueue<AreaDTO> areaQueueTeams) {
		this.areaQueueTeams = areaQueueTeams;
	}

	public void setCompetitionQueue(BlockingQueue<CompetitionDTO> competitionQueue) {
		this.competitionQueue = competitionQueue;
	}

	public AreaDTO getAreaDTO() {
		return areaQueue.poll();
	}

	public AreaDTO getAreaDTOTeam() {
		return areaQueueTeams.poll();
	}

	public CompetitionDTO getCompetitionDTO() {
		return competitionQueue.poll();
	}
	public void setCompetitionIdOptaQueue(Long idCompetition){
		if(competitionIdOptaQueue == null){
			competitionIdOptaQueue = new LinkedBlockingQueue<Long>();
		}
		competitionIdOptaQueue.add(idCompetition);
	}
	public Long getCompetitionIdOptaQueue(){
		return competitionIdOptaQueue.poll();
	}
	
	public void clear() {
		areas = new HashMap<Long, Area>();
		areaQueue = null;
		areaQueueTeams = null;
		competitionQueue = null;
	}

}
