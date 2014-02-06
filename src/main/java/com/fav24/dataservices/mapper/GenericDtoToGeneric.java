package com.fav24.dataservices.mapper;

import java.util.ArrayList;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.GenericDto;
import com.fav24.dataservices.dto.OperationDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia GenericDto y el objeto de dominio Generic.
 * 
 * @author Fav24
 *
 */
public class GenericDtoToGeneric extends Mapper<GenericDto, Generic> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Generic map(GenericDto origin) throws ServerException {

		Generic generic = new Generic();

		generic.setRequestor((Requestor) Mapper.Map(origin.getRequestor()));

		if (origin.getOps() != null) {
			generic.setOperations(new ArrayList<Operation>(origin.getOps().length));

			for (OperationDto operation : origin.getOps()) {
				generic.getOperations().add((Operation) Mapper.Map(operation));
			}
		}

		return generic;
	}
}
