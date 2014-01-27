package com.fav24.dataservices.controller.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.BaseController;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.dto.security.AccessPolicyFileDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.mapper.Mapper;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.AccessPolicyFile;
import com.fav24.dataservices.service.security.LoadAccessPolicyService;
import com.fav24.dataservices.service.security.RetrieveAccessPolicyService;

/**
 * Controla las peticiones de entrada a los servicios de gestión de la seguridad.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/accesspolicy")
public class AccessPolicyController extends BaseController {

	final static Logger logger = LoggerFactory.getLogger(AccessPolicyController.class);


	@Autowired
	protected RetrieveAccessPolicyService retrieveAccessPolicyService;

	@Autowired
	protected LoadAccessPolicyService loadAccessPolicyService;

	/**
	 * Procesa una petición de información de las políficas de acceso de una cierta entidad, o de las entidades disponibles.
	 * 
	 * @param accessPolicy La entidad de la que se desea obtener las políticas, o a las entidades a las que se tiene acceso.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/retrieve", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyDto getAccessPolicy(@RequestBody final AccessPolicyDto accessPolicy) {

		AccessPolicyDto result = null;

		try {

			result = (AccessPolicyDto)Mapper.Map(retrieveAccessPolicyService.getCurrentAccessPolicy((AccessPolicy) Mapper.Map(accessPolicy)));

		} catch (ServerException e) {

			result = new AccessPolicyDto(e);
			result.setRequestor(accessPolicy.getRequestor());
			result.getRequestor().setSystemTime(System.currentTimeMillis());
		}

		return result;
	}

	/**
	 * Procesa una petición de carga de políficas de acceso.
	 * 
	 * @param accessPolicyFile URL del fichero de definición de políticas a cargar.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/load", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyFileDto getCurrentAccessPolicy(@RequestBody final AccessPolicyFileDto accessPolicyFile) {

		AccessPolicyFileDto result = null;

		try {

			result = (AccessPolicyFileDto)Mapper.Map(loadAccessPolicyService.loadAccessPolicy((AccessPolicyFile)Mapper.Map(accessPolicyFile)));

		} catch (ServerException e) {
			
			result = new AccessPolicyFileDto(e);
			result.setRequestor(accessPolicyFile.getRequestor());
			result.getRequestor().setSystemTime(System.currentTimeMillis());
		}

		return result;
	}
}
