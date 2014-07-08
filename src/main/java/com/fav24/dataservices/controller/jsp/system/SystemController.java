package com.fav24.dataservices.controller.jsp.system;


import java.nio.file.FileSystems;
import java.nio.file.Path;
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
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.fileSystem.FileInformation;
import com.fav24.dataservices.service.fileSystem.FileSystemService;
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
	@Autowired
	private FileSystemService fileSystemService;


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
	 * 
	 * @throws ServerException 
	 */
	@RequestMapping(value = "/context", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView systemContext() throws ServerException {

		ModelAndView model = new ModelAndView("system_context");

		model.addObject("contextHome", DataServicesContext.getCurrentDataServicesContext().getApplicationHome());
		model.addObject("contextSoName", System.getProperty("os.name"));
		model.addObject("contextSoArch", System.getProperty("os.arch"));
		model.addObject("contextSoVersion", System.getProperty("os.version"));
		model.addObject("contextSoUser", System.getProperty("user.name"));
		model.addObject("contextContainerInfo", webApplicationContext.getServletContext().getServerInfo());
		AbstractList<String[]> networkIterfaces = NetworkUtils.getNetworkIterfaces();
		model.addObject("contextNetworkIterfaces", networkIterfaces);
		String externalIP = NetworkUtils.getExternalIP();
		model.addObject("contextNetworkExternalIP", externalIP == null ? "-" : externalIP);
		model.addObject("contextJavaVendor", System.getProperty("java.vm.vendor"));
		model.addObject("contextJavaVersion", System.getProperty("java.version"));
		AbstractList<AbstractList<String>> organizedClassPath = hookConfigurationService.getOrganizedClassPath();
		model.addObject("contextJavaClasspathClasspaths", organizedClassPath.get(0));
		model.addObject("contextJavaClasspathDependencies", organizedClassPath.get(1));

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

	/**
	 * Retorna una lista con el conjunto de ficheros contenidos en la ruta indicada, que cumplen con el 
	 * patrón indicado, y se corresponden con el parámetro @param directoriesOnly. 
	 * 
	 * @param path Ruta de la que se desea obtener la información.
	 * @param parent Si true, retorna la lista del directorio padre de la ruta indicada, si false o <code>null</code>, la de la propia ruta.
	 * @param pattern Patrón que deben cumplir los archivos contenidos en la lista.
	 * @param directoriesOnly True o false en función de si se desean únicamente directorios o no.
	 * @param filesOnly True o false en función de si se desean únicamente ficheros o no.
	 * 
	 * @return una lista con el conjunto de ficheros contenidos en la ruta indicada, que cumplen con el 
	 * patrón indicado, y se corresponden con el parámetro @param directoriesOnly.
	 */
	@RequestMapping(value = "/fileInformationList", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView systemFileInformationList(@ModelAttribute(value="path") String path, 
			@ModelAttribute(value="parent") Boolean parent,
			@ModelAttribute(value="pattern") String pattern, 
			@ModelAttribute(value="directoriesOnly") Boolean directoriesOnly,
			@ModelAttribute(value="filesOnly") Boolean filesOnly) {

		ModelAndView model = new ModelAndView("system_file_list");

		Path basePath = FileSystems.getDefault().getPath(path).toAbsolutePath();
		if (parent != null && parent) {
			
			Path parentPath = basePath.getParent();
			
			if (parentPath != null) {
				
				basePath = parentPath;
			}
		}
		AbstractList<FileInformation> fileInformationList = fileSystemService.getFileInformationList(basePath, pattern, directoriesOnly == null ? false : directoriesOnly, filesOnly == null ? false : filesOnly);

		model.addObject("basePath", basePath.toString());
		model.addObject("fileInformationList", fileInformationList);

		return model;
	}
}
