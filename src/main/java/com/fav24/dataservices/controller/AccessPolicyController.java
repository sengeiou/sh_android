package com.fav24.dataservices.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.services.AccessPolicyService;
import com.fav24.dataservices.to.AccessPolicyResultTO;
import com.fav24.dataservices.to.AccessPolicyTO;

/**
 * Controla las peticiones de entrada a un conjunto de entidades de datos.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/accesspolicy")
public class AccessPolicyController extends BaseController {

	final static Logger logger = LoggerFactory.getLogger(AccessPolicyController.class);


	@Autowired
	protected AccessPolicyService accessPolicyService;

	/**
	 * Procesa una petición de información de las políficas de acceso de una cierta entidad, o de las entidades disponibles.
	 * 
	 * @param accessPolicy La entidad de la que se desea obtener las políticas, o a las entidades a las que se tiene acceso.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/retrieve", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyResultTO getAccessPolicy(@RequestBody final AccessPolicyTO accessPolicy) {

		return accessPolicyService.getCurrentAccessPolicy(accessPolicy);
	}
	
	/**
	 * Procesa una petición de información de las políficas de acceso de una cierta entidad, o de las entidades disponibles.
	 * 
	 * @param accessPolicy La entidad de la que se desea obtener las políticas, o a las entidades a las que se tiene acceso.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/loadconfiguration", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	AccessPolicyResultTO getCurrentAccessPolicy(@RequestBody final AccessPolicyTO accessPolicy) {
		
		return accessPolicyService.loadAccessPolicy(accessPolicy);
	}
}
