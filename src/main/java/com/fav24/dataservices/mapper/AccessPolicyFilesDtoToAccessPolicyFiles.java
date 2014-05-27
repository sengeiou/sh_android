package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de transferencia AccessPolicyFileDto y el objeto de dominio AccessPolicyFile.
 */
public class AccessPolicyFilesDtoToAccessPolicyFiles extends Mapper<AccessPolicyFilesDto, RemoteFiles> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RemoteFiles map(AccessPolicyFilesDto origin) throws ServerException {

		RemoteFiles accessPolicyFiles = new RemoteFiles(origin.getPolicyFilesURLs());

		accessPolicyFiles.setRequestor((Requestor)Mapper.Map(origin.getRequestor()));

		return accessPolicyFiles;
	}
}
