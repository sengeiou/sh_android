package com.fav24.shootr.batch.optaMatches.chunk;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.MatchDAO;

@Component
public class OptaMatchWriter implements ItemWriter<OptaMatchItem> {
	private static Logger logger = Logger.getLogger(OptaMatchWriter.class);

	@Autowired
	private MatchDAO matchDAO;

	@Override
	public void write(List<? extends OptaMatchItem> items) throws Exception {
		for (OptaMatchItem item : items) {
			if (!item.getNewMatches().isEmpty()) {
				matchDAO.batchInsertMatch(item.getNewMatches());
			}
			if (!item.getUpdateMatches().isEmpty()) {
				matchDAO.batchUpdateMatch(item.getUpdateMatches());
			}
			
			if (item.getNewMatches().size() > 0 || item.getUpdateMatches().size() > 0) {
				logger.info(Thread.currentThread().getId() + " - Inserted: " + item.getNewMatches().size() + ", Updated: " + item.getUpdateMatches().size());
			}
		}
	}
}
