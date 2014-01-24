package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.dto.GenericDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio Generic y el objeto de transferencia GenericDto.
 * 
 * @author Fav24
 *
 */
public class GenericToGenericDto extends Mapper<Generic, GenericDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GenericDto map(Generic origin) {
		return null;
	}
}
