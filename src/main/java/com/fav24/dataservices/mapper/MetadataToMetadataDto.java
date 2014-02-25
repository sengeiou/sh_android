package com.fav24.dataservices.mapper;

import java.util.TreeMap;

import com.fav24.dataservices.domain.KeyItem;
import com.fav24.dataservices.domain.Metadata;
import com.fav24.dataservices.dto.FilterDto;
import com.fav24.dataservices.dto.MetadataDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio Metadata y el objeto de transferencia MetadataDto.
 * 
 * @author Fav24
 *
 */
public class MetadataToMetadataDto extends Mapper<Metadata, MetadataDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MetadataDto map(Metadata origin) throws ServerException {

		MetadataDto metadata = new MetadataDto();

		metadata.setOperation(origin.getOperation().getOperationType());
		metadata.setEntity(origin.getEntity());
		metadata.setTotalItems(origin.getTotalItems());
		metadata.setOffset(origin.getOffset());
		metadata.setItems(origin.getItems());

		if (origin.getKey() != null) {

			metadata.setKey(new TreeMap<String, Object>());

			for (KeyItem keyItem : origin.getKey()) {

				metadata.getKey().put(keyItem.getName(), keyItem.getValue());
			}
		}

		if (origin.getFilter() != null) {
			metadata.setFilter((FilterDto) Mapper.Map(origin.getFilter()));
		}

		return metadata;
	}
}
