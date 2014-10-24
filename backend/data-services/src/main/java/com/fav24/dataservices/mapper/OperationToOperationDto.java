package com.fav24.dataservices.mapper;

import java.util.HashMap;
import java.util.NavigableMap;

import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.dto.generic.MetadataDto;
import com.fav24.dataservices.dto.generic.OperationDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de dominio Operation y el objeto de transferencia OperationDto.
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
			
			@SuppressWarnings("unchecked")
			HashMap<String, Object>[] data = new HashMap[origin.getData().size()];
			NavigableMap<String, Object> attributes;
			operation.setData(data);

			for (int i=0; i<origin.getData().size(); i++) {
				
				data[i] = new HashMap<String, Object>();
				attributes = origin.getData().get(i).getAttributes();
				
				if (attributes != null) {
					data[i].putAll(attributes);
				}
			}
		}

		return operation;
	}
}
