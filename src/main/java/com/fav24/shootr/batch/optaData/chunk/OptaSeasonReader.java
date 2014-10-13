package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OptaSeasonReader implements ItemReader<OptaSeasonItem> {

	@Autowired
	private OptaDataShared loadOptaDataShared;

	@Override
	public OptaSeasonItem read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Long idCompetitionOpta = loadOptaDataShared.getCompetitionIdOptaQueue();
		if (idCompetitionOpta != null) {
			OptaSeasonItem item = new OptaSeasonItem();
			item.setIdCompetitionOpta(idCompetitionOpta);
			item.setDto(loadOptaDataShared.getSeasonsByCompetitionId().get(item.getIdCompetitionOpta()));
			return item;
		}
		return null;
	}
}
