package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.TeamDAO;

@Component
public class OptaTeamWriter implements ItemWriter<OptaTeamItem> {
	private static Logger logger = Logger.getLogger(OptaTeamWriter.class);
	
	@Autowired
	private TeamDAO teamDAO;

	@Autowired
	private OptaDataShared loadOptaDataShared;

	@Override
	public void write(List<? extends OptaTeamItem> items) throws Exception {
		for (OptaTeamItem item : items) {
			if (!item.getNewTeams().isEmpty()) {
				teamDAO.batchInsertTeam(item.getNewTeams());
				logger.info(Thread.currentThread().getId()+" - Area: " + item.getArea().getName() + ", Inserted " + item.getNewTeams().size() + " teams.");
			}
		}
	}
}
