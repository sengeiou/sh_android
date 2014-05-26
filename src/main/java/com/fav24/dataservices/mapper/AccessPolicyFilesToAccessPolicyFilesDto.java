package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.dto.RequestorDto;
import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio AccessPolicy y el objeto de transferencia AccessPolicyDto.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyFilesToAccessPolicyFilesDto extends Mapper<RemoteFiles, AccessPolicyFilesDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyFilesDto map(RemoteFiles origin) throws ServerException {
		
		AccessPolicyFilesDto accessPolicyFilesDto = new AccessPolicyFilesDto();
		
		accessPolicyFilesDto.setRequestor((RequestorDto)Mapper.Map(origin.getRequestor()));
		accessPolicyFilesDto.setPolicyFilesURLs(origin.getURLsAsStrings());
		
		return accessPolicyFilesDto;
	}
}
