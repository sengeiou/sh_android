package com.fav24.dataservices.controller.jsp.cache;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.controller.jsp.BaseJspController;
import com.fav24.dataservices.dto.ResultDto;
import com.fav24.dataservices.dto.UploadFilesDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.cache.CacheConfigurationService;
import com.fav24.dataservices.util.FileUtils;

/**
 * Controla las peticiones de entrada a las páginas de gestión de la caché.
 */
@Scope("singleton")
@Controller
@RequestMapping("/cache")
public class CacheConfigurationController extends BaseJspController {

	@Autowired
	protected CacheConfigurationService cacheConfigurationService;


	/**
	 * Muestra la lista de gestores de caché disponibles para este servicio de datos.
	 *  
	 * @return el modelo y la vista, con la lista de gestores de caché disponibles para este servicio de datos.
	 */
	@RequestMapping(value = "/availableCacheManagers", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView availableCacheManagers() {

		ModelAndView model = new ModelAndView("available_entity_cache_managers");

		model.addObject("cacheManagers", cacheConfigurationService.getCacheManagers());

		return model;
	}

	/**
	 * Muestra la configuración del gestor de caché indicado.
	 * 
	 * @param cacheManager Gestor de caché a obtener.
	 * 
	 * @return el modelo y la vista, con la configuración del gestor de caché indicado.
	 */
	@RequestMapping(value = "/cacheManagerConfiguration", method = { RequestMethod.GET })
	public ModelAndView cacheManagerConfiguration(@ModelAttribute(value="cacheManager") String cacheManager) {

		ModelAndView model = new ModelAndView("entity_cache_manager_details");

		model.addObject("cacheManagerConfiguration", cacheConfigurationService.getCacheManagerConfiguration(cacheManager));

		return model;
	}

	/**
	 * Muestra la lista de gestores de caché disponibles para este servicio de datos.
	 *  
	 * @return el modelo y la vista, con la lista de gestores de caché disponibles para este servicio de datos.
	 */
	@RequestMapping(value = "/availableCaches", method = { RequestMethod.GET })
	public ModelAndView availableCaches(@ModelAttribute(value="cacheManager") String cacheManager) {

		ModelAndView model = new ModelAndView("available_entity_caches");

		model.addObject("cacheManagers", cacheConfigurationService.getCacheManagers());

		return model;
	}

	/**
	 * Muestra la configuración de caché de una entidad.
	 * 
	 * @param cacheManager Gestor de caché que contiene la caché de la entidad indicada.
	 * @param cache Caché de entidad de la que se mostrará la configuración de caché.
	 * 
	 * @return el modelo y la vista, con la configuración de caché de una entidad.
	 */
	@RequestMapping(value = "/cacheConfiguration", method = { RequestMethod.GET })
	public ModelAndView cacheConfiguration(@ModelAttribute(value="cacheManager") String cacheManager, @ModelAttribute(value="cache") String cache) {

		ModelAndView model = new ModelAndView("entity_cache_details");

		model.addObject("cacheManagerConfiguration", cacheConfigurationService.getCacheManagerConfiguration(cacheManager));
		model.addObject("cacheConfiguration", cacheConfigurationService.getCacheConfiguration(cacheManager, cache));

		return model;
	}

	/**
	 * Muestra el formulario de carga de ficheros de configuración de caché.
	 * 
	 * @return el nombre del formulario de carga de ficheros de configuración de caché.
	 */
	@RequestMapping(value = "/cacheConfigurationUpload.show", method = { RequestMethod.GET, RequestMethod.POST })
	public String displayForm() {
		return "cache_configuration_upload";
	}

	/**
	 * Carga el contenido de los ficheros seleccionados en el formulario.
	 * 
	 * @param uploadCacheConfiguration Conjunto de ficheros a cargar.
	 * @param map Estructura con los nombres de los ficheros cargados con éxito.
	 * 
	 * @return el nombre del formulario que mostrará los ficheros cargados con éxito.
	 */
	@RequestMapping(value = "/cacheConfigurationUpload.save", method = { RequestMethod.POST })
	public String cacheConfigurationUpload(@ModelAttribute("uploadCacheConfigurationFiles") UploadFilesDto uploadCacheConfiguration, Model map) {

		List<MultipartFile> files = uploadCacheConfiguration.getFiles();
		List<Boolean> filesAsDefault = uploadCacheConfiguration.getFilesAsDefault();

		List<String> filesOK = new ArrayList<String>();
		List<String> filesKO = new ArrayList<String>();
		List<String> filesErrors = new ArrayList<String>();

		if (null != files && files.size() > 0) {

			int i = 0;
			for (MultipartFile multipartFile : files) {

				if (!multipartFile.isEmpty()) {

					String fileName = multipartFile.getOriginalFilename();
					Boolean fileAsDefault = filesAsDefault == null ? false : filesAsDefault.get(i++);


					InputStream inputStream = null;

					try {

						if (fileAsDefault != null && fileAsDefault) {

							inputStream = new FileInputStream(FileUtils.createOrReplaceExistingFile(multipartFile, CacheConfigurationService.CACHE_FILES_RELATIVE_LOCATION));
						}
						else {

							inputStream = multipartFile.getInputStream();
						}

						cacheConfigurationService.loadCacheConfiguration(inputStream);

						filesOK.add(fileName);
					} catch (ServerException e) {
						filesKO.add(fileName);
						filesErrors.add("[" + e.getErrorCode() + "] " + e.getMessage());
					} catch (IOException e) {
						filesKO.add(fileName);
						filesErrors.add(e.getMessage());
					}
				}
			}
		}

		map.addAttribute("filesOK", filesOK);
		map.addAttribute("filesKO", filesKO);
		map.addAttribute("filesErrors", filesErrors);

		return "cache_configuration_uploaded";
	}

	/**
	 * Carga las configuraciones de caché por defecto, contenidas en <dataservices.home>.
	 * 
	 * @param map Estructura con los atributos del estado de la operación.
	 * 
	 * @return la vista del resultado de la carga.
	 * 
	 * @throws ServerException 
	 */
	@RequestMapping(value = "/loadDefault", method = { RequestMethod.GET, RequestMethod.POST })
	public String loadDefault(Model map) throws ServerException {

		cacheConfigurationService.loadDefaultCacheConfiguration();

		map.addAttribute("title", "Carga de las configuraciones de caché por defecto.");
		map.addAttribute("message", "Las configuraciones de caché han sido cargadas con éxito.");

		return "error_pages/server_success";
	}

	/**
	 * Elimina todas la configuraciones de caché.
	 *  
	 * @param map Estructura con los atributos del estado de la operación.
	 *  
	 * @return la vista del resultado de la operación.
	 *
	 * @throws ServerException 
	 */
	@RequestMapping(value = "/dropSystemCache", method = { RequestMethod.GET, RequestMethod.POST })
	public String dropSystemCache(Model map) throws ServerException {

		cacheConfigurationService.dropSystemCache();

		map.addAttribute("title", "Caché desactivada.");
		map.addAttribute("message", "Todas las cachés asociadas a entidades, han sido eliminadas.");

		return "error_pages/server_success";
	}

	/**
	 * Inicializa la configuración de caché de una entidad.
	 * 
	 * @param cacheManager Gestor de caché que contiene la caché de la entidad indicada.
	 * @param cache Caché de entidad a inicializar.
	 * 
	 * @return el resultado de la ejecución de la operación.
	 */
	@RequestMapping(value = "/resetCache", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResultDto resetCache(@RequestBody Map<String, String> parameters) {

		ResultDto result = new ResultDto();
		String cacheManager = parameters.get("cacheManager");
		String cache = parameters.get("cache");

		try {
			cacheConfigurationService.resetCache(cacheManager, cache);

			result.setResult(ResultDto.RESULT_OK);
			result.setMessage(String.format("La caché <%s> del gestor de caché <%s>, ha sido inicializada correctamente.", cache, cacheManager));
		} 
		catch (ServerException e) {

			e.setExplanation("La operación no se ha aplicado.");

			result.setResult(ResultDto.RESULT_ERROR);
			result.setResultCode(e.getErrorCode());
			result.setMessage(String.format("No ha sido posible inicializar la caché <%s> del gestor de caché <%s>.", cache, cacheManager));
			result.setExplanation(e.getMessage());
		}

		return result;
	}
}
