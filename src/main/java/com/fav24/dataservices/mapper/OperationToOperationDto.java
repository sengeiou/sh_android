package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.dto.DataItemDto;
import com.fav24.dataservices.dto.MetadataDto;
import com.fav24.dataservices.dto.OperationDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio Operation y el objeto de transferencia OperationDto.
 * 
 * @author Fav24
 *
 */
public class OperationToOperationDto extends Mapper<Operation, OperationDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OperationDto map(Operation origin) throws ServerException {

		OperationDto operation = new OperationDto();

		operation.setMetadata((MetadataDto) Mapper.Map(origin.getMetadata()));

		if (origin.getData() != null) {
			operation.setData(new DataItemDto[origin.getData().size()]);

			for (int i=0; i<origin.getData().size(); i++) {
				operation.getData()[i] = (DataItemDto) Mapper.Map(origin.getData().get(i));
			}
		}

		return operation;
	}
}
