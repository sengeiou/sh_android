package com.fav24.dataservices.mapper;

import java.util.Map;
import java.util.TreeMap;

import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAttribute;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.domain.policy.EntityFilter;
import com.fav24.dataservices.domain.policy.EntityKey;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.dto.policy.EntityAccessPolicyDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio EntityAccessPolicy y el objeto de transferencia EntityAccessPolicyDtoElement.
 * 
 * @author Fav24
 *
 */
public class EntityAccessPolicyToEntityAccessPolicyDto extends Mapper<EntityAccessPolicy, EntityAccessPolicyDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityAccessPolicyDto map(EntityAccessPolicy origin) {

		//Entity
		EntityAccessPolicyDto entityAccessPolicy = new EntityAccessPolicyDto(origin.getName().getAlias());

		//Operaciones permitidas
		String[] allowedOperations = new String[origin.getAllowedOperations().size()];

		int i=0;
		for (OperationType operationType : origin.getAllowedOperations()) {
			allowedOperations[i++] = operationType.getOperationType();
		}
		entityAccessPolicy.setAllowedOperations(allowedOperations);

		//Attributos disponibles
		Map<String, String> attributes = new TreeMap<String, String>();

		for (EntityDataAttribute attribute : origin.getData().getData()) {
			attributes.put(attribute.getAlias(), attribute.getDirection().getDirection());
		}
		entityAccessPolicy.setAttributes(attributes);

		//Juego de claves disponibles
		String[][] keys = new String[origin.getKeys().getKeys().size()][];
		i=0;
		int j=0;
		for (EntityKey key : origin.getKeys().getKeys()) {

			keys[i] = new String[key.getKey().size()];
			j=0;
			for (EntityAttribute keyAttribute : key.getKey()) {
				keys[i][j++] = keyAttribute.getAlias();
			}
			i++;
		}
		entityAccessPolicy.setKeys(keys);

		//Juego de filtros disponibles
		if (origin.getFilters() != null) {
			String[][] filters = new String[origin.getFilters().getFilters().size()][];
			i=0;
			j=0;
			for (EntityFilter filter : origin.getFilters().getFilters()) {

				filters[i] = new String[filter.getFilter().size()];
				j=0;
				for (EntityAttribute filterAttribute : filter.getFilter()) {
					filters[i][j++] = filterAttribute.getAlias();
				}
				i++;
			}
			entityAccessPolicy.setFilters(filters);
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
