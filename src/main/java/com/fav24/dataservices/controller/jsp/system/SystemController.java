package com.fav24.dataservices.controller.jsp.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.controller.jsp.BaseJspController;

/**
 * Controla las peticiones de entrada a la consola de monitorización.
 */
@Controller
@RequestMapping("/system")
public class SystemController extends BaseJspController {

	final static Logger logger = LoggerFactory.getLogger(SystemController.class);


	/**
	 * Muestra la consola de información del estado de los recursos del sistema.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 y 24 horas.
	 * 
	 * @return la vista de monitorización del sistema.
	 */
	@RequestMapping(value = "/monitor", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView systemInformation(@ModelAttribute(value="period") Long period, @ModelAttribute(value="timeRange") Long timeRange) {

		ModelAndView model = new ModelAndView("system_monitor");

		model.addObject("period", period);
		model.addObject("timeRange", timeRange);

		return model;
	}
	
	/**
	 * Muestra el diagrama de carga de trabajo del servidor.
	 * 
	 * @return el diagrama de carga de trabajo del servidor.
	 */
	@RequestMapping(value = "/workload", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView systemWorkload() {
		
		ModelAndView model = new ModelAndView("system_workload");
		
		return model;
	}
}
