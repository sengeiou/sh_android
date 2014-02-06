package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.dto.GenericDto;
import com.fav24.dataservices.dto.OperationDto;
import com.fav24.dataservices.dto.RequestorDto;
import com.fav24.dataservices.exception.ServerException;


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
	protected GenericDto map(Generic origin) throws ServerException {

		GenericDto generic = new GenericDto();

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
