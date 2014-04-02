package com.fav24.dataservices.controller.rest.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.AccessPolicyFiles;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.mapper.Mapper;
import com.fav24.dataservices.service.security.LoadAccessPolicyService;
import com.fav24.dataservices.service.security.RetrieveAccessPolicyService;

/**
 * Controla las peticiones de entrada a los servicios de gestión de la seguridad.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/accesspolicy")
public class AccessPolicyController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(AccessPolicyController.class);

	private static final String MESSAGE_ACCESS_POLICIES_RETRIEVED_OK = "La información de políticas de acceso, se retornó correctamente.";
	private static final String MESSAGE_ACCESS_POLICY_FILES_LOADED_OK = "Los ficheros de políticas de acceso, se cargaron correctamente.";

	@Autowired
	protected RetrieveAccessPolicyService retrieveAccessPolicyService;

	@Autowired
	protected LoadAccessPolicyService loadAccessPolicyService;

	/**
	 * Procesa una petición de información de las políficas de acceso de una cierta entidad, o de las entidades disponibles.
	 * 
	 * @param accessPolicy Las entidades de las que se desea obtener las políticas, o las entidades a las que se tiene acceso.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/retrieve", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyDto getAccessPolicy(@RequestBody final AccessPolicyDto accessPolicy) {

		AccessPolicyDto result = null;

		try {

			result = (AccessPolicyDto)Mapper.Map(retrieveAccessPolicyService.getCurrentAccessPolicy((AccessPolicy) Mapper.Map(accessPolicy)));

			result.setStatusCode(BaseRestController.OK);
			result.setStatusMessage(MESSAGE_ACCESS_POLICIES_RETRIEVED_OK);
		} catch (ServerException e) {

			result = new AccessPolicyDto(e);
			result.setRequestor(accessPolicy.getRequestor());
			
			e.log(logger, false);
		}

		result.getRequestor().setSystemTime(System.currentTimeMillis());

		return result;
	}

	/**
	 * Procesa una petición de carga de políficas de acceso.
	 * 
	 * @param accessPolicyFiles URLs de los ficheros de definición de políticas a cargar.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/load", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyFilesDto getCurrentAccessPolicy(@RequestBody final AccessPolicyFilesDto accessPolicyFiles) {

		AccessPolicyFilesDto result = null;

		try {

			result = (AccessPolicyFilesDto)Mapper.Map(loadAccessPolicyService.loadAccessPolicy((AccessPolicyFiles)Mapper.Map(accessPolicyFiles)));

			result.setStatusCode(BaseRestController.OK);
			result.setStatusMessage(MESSAGE_ACCESS_POLICY_FILES_LOADED_OK);
		} catch (ServerException e) {

			result = new AccessPolicyFilesDto(e);
			result.setRequestor(accessPolicyFiles.getRequestor());
			
			e.log(logger, false);
		}

		result.getRequestor().setSystemTime(System.currentTimeMillis());

		return result;
	}
}
