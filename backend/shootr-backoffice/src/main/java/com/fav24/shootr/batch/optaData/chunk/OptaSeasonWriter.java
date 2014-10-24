package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.SeasonDAO;

@Component
public class OptaSeasonWriter implements ItemWriter<OptaSeasonItem> {
	private static Logger logger = Logger.getLogger(OptaSeasonWriter.class);

	@Autowired
	private SeasonDAO seasonDAO;

	@Override
	public void write(List<? extends OptaSeasonItem> items) throws Exception {
		for (OptaSeasonItem item : items) {
			if (!item.getNewSeasons().isEmpty()) {
				seasonDAO.batchInsertSeason(item.getNewSeasons());
			}

			if (!item.getUpdateSeasons().isEmpty()) {
				// season con el lastUpdate actualizado
				seasonDAO.batchUpdateSeason(item.getUpdateSeasons());
			}

			logger.info(Thread.currentThread().getId() + " - Competition: " + item.getCompetition().getName() + ", Inserted " + item.getNewSeasons().size() + ", Updated: "
					+ item.getUpdateSeasons().size() + "seasons.");
		}
	}
}
