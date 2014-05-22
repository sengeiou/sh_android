package com.fav24.dataservices.mapper;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Metadata;
import com.fav24.dataservices.domain.security.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.dto.generic.MetadataDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de transferencia MetadataDto y el objeto de dominio Metadata.
 */
public class MetadataDtoToMetadata extends Mapper<MetadataDto, Metadata> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Metadata map(MetadataDto origin) throws ServerException {

		Metadata metadata = new Metadata();

		OperationType opperationType = OperationType.fromString(origin.getOperation());
		
		if (opperationType == null) {
			throw new ServerException(ERROR_MAPPER_UNSUPORTED_VALUE, String.format(ERROR_MAPPER_UNSUPORTED_VALUE_MESSAGE, origin.getOperation()));	
		}
		
		metadata.setOperation(opperationType);
		metadata.setEntity(origin.getEntity());
		metadata.setIncludeDeleted(origin.getIncludeDeleted());
		metadata.setTotalItems(origin.getTotalItems());
		metadata.setOffset(origin.getOffset());
		metadata.setItems(origin.getItems());

		if (origin.getKey() != null) {

			metadata.setKey(new ArrayList<KeyItem>(origin.getKey().size()));

			for (Entry<String, Object> keyItem : origin.getKey().entrySet()) {

				metadata.getKey().add(new KeyItem(keyItem.getKey(), keyItem.getValue()));
			}
		}

		if (origin.getFilter() != null) {
			metadata.setFilter((Filter) Mapper.Map(origin.getFilter()));
		}

		return metadata;
	}
}
