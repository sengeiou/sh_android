package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.RequestorDtoElement;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia RequestorDto y el objeto de dominio Requestor.
 * 
 * @author Fav24
 *
 */
public class RequestorDtoElementToRequestor extends Mapper<RequestorDtoElement, Requestor> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Requestor map(RequestorDtoElement origin) {
		
		Requestor requestor = new Requestor();
		
		requestor.setIdDevice(origin.getReq()[RequestorDtoElement.POSITION_ID_DEVICE]);
		requestor.setAppVersion(origin.getReq()[RequestorDtoElement.POSITION_APP_VERSION]);
		requestor.setIdPlatform(origin.getReq()[RequestorDtoElement.POSITION_ID_PLATFORM]);
		requestor.setIdUser(origin.getReq()[RequestorDtoElement.POSITION_ID_USER]);
		requestor.setTime(origin.getReq()[RequestorDtoElement.POSITION_SYSTEM_TIME]);
		
		return requestor;
	}
}
