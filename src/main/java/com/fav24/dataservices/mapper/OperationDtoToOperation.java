package com.fav24.dataservices.mapper;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.fav24.dataservices.domain.DataItem;
import com.fav24.dataservices.domain.Metadata;
import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.dto.OperationDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia OperationDto y el objeto de dominio Operation.
 * 
 * @author Fav24
 *
 */
public class OperationDtoToOperation extends Mapper<OperationDto, Operation> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation map(OperationDto origin) throws ServerException {

		Operation operation = new Operation();
		
		operation.setMetadata((Metadata) Mapper.Map(origin.getMetadata()));

		if (origin.getData() != null) {
			operation.setData(new ArrayList<DataItem>(origin.getData().length));

			for (Map<String, Object> dataItemMap : origin.getData()) {
				
				DataItem dataItem = new DataItem(new TreeMap<String, Object>(dataItemMap));
				
				operation.getData().add(dataItem);
			}
		}

		return operation;
	}
}
