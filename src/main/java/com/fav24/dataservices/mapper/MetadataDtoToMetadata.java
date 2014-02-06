package com.fav24.dataservices.mapper;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.fav24.dataservices.domain.Filter;
import com.fav24.dataservices.domain.KeyItem;
import com.fav24.dataservices.domain.Metadata;
import com.fav24.dataservices.dto.MetadataDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.EntityAccessPolicy.OperationType;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia MetadataDto y el objeto de dominio Metadata.
 * 
 * @author Fav24
 *
 */
public class MetadataDtoToMetadata extends Mapper<MetadataDto, Metadata> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Metadata map(MetadataDto origin) throws ServerException {

		Metadata metadata = new Metadata();

		metadata.setOperation(OperationType.fromString(origin.getOperation()));
		metadata.setEntity(origin.getEntity());
		metadata.setEntitySize(origin.getEntitySize());
		metadata.setItems(origin.getItems());

		if (origin.getKey() != null) {

			metadata.setKey(new ArrayList<KeyItem>(origin.getKey().size()));

			for (Entry<String, String> keyItem : origin.getKey().entrySet()) {

				metadata.getKey().add(new KeyItem(keyItem.getKey(), keyItem.getValue()));
			}
		}

		if (origin.getFilter() != null) {
			metadata.setFilter((Filter) Mapper.Map(origin.getFilter()));
		}

		return metadata;
	}
}
