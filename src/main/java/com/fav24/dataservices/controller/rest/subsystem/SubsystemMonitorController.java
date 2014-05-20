package com.fav24.dataservices.controller.rest.subsystem;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.datasources.DataSourcesService;


/**
 * Controla las peticiones de entrada a los servicios de gestión del subsistema.
 */
@Controller
@RequestMapping("/subsystem")
public class SubsystemMonitorController extends BaseRestController {

	private static final Logger logger = LoggerFactory.getLogger(SubsystemMonitorController.class);

	@Autowired
	private DataSourcesService dataSourcesService;


	/**
	 * Procesa una petición de la fecha y hora del subsistema en milisegundos desde epoch.
	 * 
	 * @return la fecha y hora del subsistema en milisegundos desde epoch.
	 */
	@RequestMapping(value = "/time", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Long getTime() {

		try {

			return dataSourcesService.getDataServiceDataSourceTime();

		} catch (ServerException e) {

			e.log(logger, false);
		}

		return null;
	}
}
