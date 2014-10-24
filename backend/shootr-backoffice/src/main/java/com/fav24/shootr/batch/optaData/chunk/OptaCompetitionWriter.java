package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.domain.Competition;

@Component
public class OptaCompetitionWriter implements ItemWriter<Competition> {
	private static Logger logger = Logger.getLogger(OptaCompetitionWriter.class);
	
	@Autowired
	private CompetitionDAO competitionDAO;

	@Autowired
	private OptaDataShared loadOptaDataShared;

	@Override
	public void write(List<? extends Competition> items) throws Exception {
		for (Competition item : items) {
			if (item != null) {
				competitionDAO.insertCompetition(item);
				logger.info("inserted "+item);
			}
		}
	}
}
