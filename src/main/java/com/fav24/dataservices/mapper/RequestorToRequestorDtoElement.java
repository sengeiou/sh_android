package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.RequestorDtoElement;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio Requestor y el objeto de transferencia RequestorDto.
 * 
 * @author Fav24
 *
 */
public class RequestorToRequestorDtoElement extends Mapper<Requestor, RequestorDtoElement> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RequestorDtoElement map(Requestor origin) {
		
		RequestorDtoElement requestor = new RequestorDtoElement(origin.getIdDevice(), origin.getIdUser(), origin.getIdPlatform(), origin.getAppVersion(), origin.getTime());
		
		return requestor;
	}
}
