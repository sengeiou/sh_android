package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;

@Component
public class OptaCompetitionReader implements ItemReader<CompetitionDTO>{

	@Autowired
	private OptaDataShared loadOptaDataShared;
	
	@Override
	public CompetitionDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return loadOptaDataShared.getCompetitionDTO();
	}

}
