package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.security.AccessPolicyFiles;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio AccessPolicy y el objeto de transferencia AccessPolicyDto.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyFilesToAccessPolicyFilesDto extends Mapper<AccessPolicyFiles, AccessPolicyFilesDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyFilesDto map(AccessPolicyFiles origin) {
		return null;
	}
}
