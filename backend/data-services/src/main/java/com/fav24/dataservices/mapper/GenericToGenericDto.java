package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.dto.RequestorDto;
import com.fav24.dataservices.dto.generic.GenericDto;
import com.fav24.dataservices.dto.generic.OperationDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de dominio Generic y el objeto de transferencia GenericDto.
 */
public class GenericToGenericDto extends Mapper<Generic, GenericDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GenericDto map(Generic origin) throws ServerException {

		GenericDto generic = new GenericDto();

		generic.setAlias(origin.getAlias());
		generic.setRequestor((RequestorDto) Mapper.Map(origin.getRequestor()));

		if (origin.getOperations() != null) {

			generic.setOps(new OperationDto[origin.getOperations().size()]);

			for (int i=0; i<origin.getOperations().size(); i++) {
				generic.getOps()[i] = (OperationDto) Mapper.Map(origin.getOperations().get(i));
			}
		}

		return generic;
	}
}
