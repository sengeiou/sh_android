package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;

@Component
public class OptaAreaReader implements ItemReader<AreaDTO>{

	@Autowired
	private OptaDataShared loadOptaDataShared;
	
	@Override
	public AreaDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return loadOptaDataShared.getAreaDTO();
	}

}
