package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.dto.cache.EntityCacheDto;


/**
 * Clase encargada del mapeo entre el objeto de dominio EntityCache y el objeto de transferencia EntityCacheDto.
 */
public class EntityCacheToEntityCacheDto extends Mapper<EntityCache, EntityCacheDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityCacheDto map(EntityCache origin) {

		EntityCacheDto entityCache = new EntityCacheDto();

		entityCache.setEntityAlias(origin.getAlias());
		entityCache.setMaxBytesLocalHeap(origin.getMaxBytesLocalHeap());
		entityCache.setMaxBytesLocalDisk(origin.getMaxBytesLocalDisk());

		return entityCache;
	}
}
