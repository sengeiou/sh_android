package com.fav24.dataservices.controller.jsp.system;


import java.util.AbstractList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.controller.jsp.BaseJspController;
import com.fav24.dataservices.service.hook.HookConfigurationService;
import com.fav24.dataservices.util.NetworkUtils;

/**
 * Controla las peticiones de entrada a la consola de monitorización.
 */
@Scope("singleton")
@Controller
@RequestMapping("/system")
public class SystemController extends BaseJspController {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private HookConfigurationService hookConfigurationService;

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
	 * Muestra el contexto de ejecución del servidor.
	 * 
	 * @return el el contexto de ejecución del servidor.
	 */
	@RequestMapping(value = "/context", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView systemContext() {
		
		ModelAndView model = new ModelAndView("system_context");
		
		model.addObject("context.home", DataServicesContext.getCurrentDataServicesContext().getApplicationHome());
		model.addObject("context.so.name", System.getProperty("os.name"));
		model.addObject("context.so.arch", System.getProperty("os.arch"));
		model.addObject("context.so.version", System.getProperty("os.version"));
		model.addObject("context.so.user", System.getProperty("user.name"));
		model.addObject("context.container.info", webApplicationContext.getServletContext().getServerInfo());
		AbstractList<String[]> internalIPs = NetworkUtils.getInternalIPs();
		model.addObject("context.network.internal-ip", internalIPs);
		String externalIP = NetworkUtils.getExternalIP();
		model.addObject("context.network.external-ip", externalIP == null ? "-" : externalIP);
		model.addObject("context.java.vendor", System.getProperty("java.vm.vendor"));
		model.addObject("context.java.version", System.getProperty("java.version"));
		AbstractList<AbstractList<String>> organizedClassPath = hookConfigurationService.getOrganizedClassPath();
		model.addObject("context.java.classpath.classpaths", organizedClassPath.get(0));
		model.addObject("context.java.classpath.dependencies", organizedClassPath.get(1));
		
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
