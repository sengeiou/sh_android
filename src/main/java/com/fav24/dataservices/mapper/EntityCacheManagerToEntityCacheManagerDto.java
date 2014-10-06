package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.dto.cache.EntityCacheDto;
import com.fav24.dataservices.dto.cache.EntityCacheManagerDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de dominio EntityCacheManager y el objeto de transferencia EntityCacheManagerDto.
 */
public class EntityCacheManagerToEntityCacheManagerDto extends Mapper<EntityCacheManager, EntityCacheManagerDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityCacheManagerDto map(EntityCacheManager origin) throws ServerException {

		EntityCacheManagerDto entityCacheManager = new EntityCacheManagerDto();

		entityCacheManager.setName(origin.getName());
		entityCacheManager.setMaxBytesLocalHeap(origin.getMaxBytesLocalHeap());
		entityCacheManager.setMaxBytesLocalDisk(origin.getMaxBytesLocalDisk());

		EntityCacheDto[] caches = new EntityCacheDto[origin.getEntitiesCacheConfigurations().size()];
		
		int i=0;
		for (EntityCache entityCache : origin.getEntitiesCacheConfigurations()) {
			
			EntityCacheDto entityCacheDto = (EntityCacheDto)Mapper.Map(entityCache);
			
			entityCacheDto.setManagerName(origin.getName());
			
			caches[i++] = entityCacheDto;
		}
		
		entityCacheManager.setCaches(caches);
		
		return entityCacheManager;
	}
}
