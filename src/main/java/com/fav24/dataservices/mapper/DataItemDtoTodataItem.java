package com.fav24.dataservices.mapper;

import java.util.TreeMap;

import com.fav24.dataservices.domain.DataItem;
import com.fav24.dataservices.dto.DataItemDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia DataItemDto y el objeto de dominio DataItem.
 * 
 * @author Fav24
 *
 */
public class DataItemDtoTodataItem extends Mapper<DataItemDto, DataItem> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataItem map(DataItemDto origin) throws ServerException {

		DataItem dataItem = new DataItem();

		if (origin.getAttributes() != null) {
			dataItem.setAttributes(new TreeMap<String, Object>(origin.getAttributes()));
		}

		return dataItem;
	}
}
