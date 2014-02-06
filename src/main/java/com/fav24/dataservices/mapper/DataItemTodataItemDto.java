package com.fav24.dataservices.mapper;

import java.util.TreeMap;

import com.fav24.dataservices.domain.DataItem;
import com.fav24.dataservices.dto.DataItemDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio DataItem y el objeto de transferencia DataItemDto.
 * 
 * @author Fav24
 *
 */
public class DataItemTodataItemDto extends Mapper<DataItem, DataItemDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataItemDto map(DataItem origin) throws ServerException {

		DataItemDto dataItem = new DataItemDto();

		if (origin.getAttributes() != null) {
			dataItem.setAttributes(new TreeMap<String, Object>(origin.getAttributes()));
		}

		return dataItem;
	}
}
