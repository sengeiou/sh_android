package com.fav24.dataservices.mapper;

import java.util.Map.Entry;

import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAttribute;
import com.fav24.dataservices.domain.policy.EntityData;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.domain.policy.EntityFilter;
import com.fav24.dataservices.domain.policy.EntityFilters;
import com.fav24.dataservices.domain.policy.EntityKey;
import com.fav24.dataservices.domain.policy.EntityKeys;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.domain.policy.EntityDataAttribute.Direction;
import com.fav24.dataservices.dto.policy.EntityAccessPolicyDto;


/**
 * Clase encargada del mapeo entre el objeto de transferencia EntityAccessPolicyDtoElement y el objeto de dominio EntityAccessPolicy.
 */
public class EntityAccessPolicyDtoToEntityAccessPolicy extends Mapper<EntityAccessPolicyDto, EntityAccessPolicy> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityAccessPolicy map(EntityAccessPolicyDto origin) {

		//Entity
		EntityAccessPolicy entityAccessPolicy = new EntityAccessPolicy();
		entityAccessPolicy.setName(new EntityAttribute(origin.getEntityAlias(), null));

		//Operaciones permitidas
		if (origin.getAllowedOperations() != null) {

			for (String operationType : origin.getAllowedOperations()) {
				entityAccessPolicy.getAllowedOperations().add(OperationType.fromString(operationType));
			}
		}

		//Attributos disponibles
		if (origin.getAttributes() != null) {

			EntityData entityData = new EntityData();
			entityAccessPolicy.setData(entityData);

			for (Entry<String, String> attribute : origin.getAttributes().entrySet()) {

				EntityDataAttribute entityDataAttribute = new EntityDataAttribute(attribute.getKey(), null, Direction.fromString(attribute.getValue()));
				entityData.addDataAttribute(entityDataAttribute);
			}
		}

		//Juego de claves disponibles
		if (origin.getKeys() != null) {

			EntityKeys keys = new EntityKeys();
			entityAccessPolicy.setKeys(keys);

			for (String[] key : origin.getKeys()) {
				EntityKey entityKey = new EntityKey();

				for (String attributeAlias : key) {
					entityKey.addKeyAttribute(new EntityAttribute(attributeAlias, null));
				}

				keys.getKeys().add(entityKey);
			}
		}

		//Juego de filtros disponibles
		if (origin.getFilters() != null) {
			EntityFilters filters = new EntityFilters();
			entityAccessPolicy.setFilters(filters);

			for (String[] filter : origin.getFilters()) {
				EntityFilter entityFilter = new EntityFilter();

				for (String attributeAlias : filter) {
					entityFilter.addFilterAttribute(new EntityAttribute(attributeAlias, null));
				}

				filters.getFilters().add(entityFilter);
			}
		}

		//Indica si el acceso es únicamente mediante claves o también filtros.
		entityAccessPolicy.setOnlyByKey(origin.getOnlyByKey());

		//Indica si el acceso es únicamente mediante los filtros definidos o no.
		entityAccessPolicy.setOnlySpecifiedFilters(origin.getOnlySpecifiedFilters());

		//Indica el número máximo de elemento retornados por petición.
		entityAccessPolicy.setMaxPageSize(origin.getMaxPageSize());


		return entityAccessPolicy;
	}
}
