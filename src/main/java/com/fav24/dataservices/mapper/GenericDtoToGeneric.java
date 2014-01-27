package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.dto.GenericDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia Generic y el objeto de dominio GenericDto.
 * 
 * @author Fav24
 *
 */
public class GenericDtoToGeneric extends Mapper<GenericDto, Generic> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Generic map(GenericDto origin) {
		return null;
	}
}
