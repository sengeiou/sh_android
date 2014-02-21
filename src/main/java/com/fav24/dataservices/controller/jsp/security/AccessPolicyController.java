package com.fav24.dataservices.controller.jsp.security;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.dto.security.UploadPolicyFilesDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.security.LoadAccessPolicyService;
import com.fav24.dataservices.service.security.RetrieveAccessPolicyService;

/**
 * Controla las peticiones de entrada a las páginas de gestión de la seguridad.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/accesspolicy")
public class AccessPolicyController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(AccessPolicyController.class);

	@Autowired
	protected RetrieveAccessPolicyService retrieveAccessPolicyService;
	
	@Autowired
	protected LoadAccessPolicyService loadAccessPolicyService;


	/**
	 * Muestra la lista de entidades disponibles para este servicio de datos.
	 *  
	 * @return el modelo y la vista, con la lista de entidades disponibles para este servicio de datos.
	 */
	@RequestMapping(value = "/availableEntities", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView availableEntities() {

		ModelAndView model = new ModelAndView("available_entities");

		model.addObject("entities", retrieveAccessPolicyService.getPublicEntities());

		return model;
	}

	/**
	 * Muestra el conjunto de políticas de una entidad.
	 * 
	 * @return el modelo y la vista, con el conjunto de políticas de una entidad.
	 */
	@RequestMapping(value = "/entityPolicies", method = { RequestMethod.GET })
	public ModelAndView entityPolicies(@ModelAttribute(value="entity") String entity) {

		ModelAndView model = new ModelAndView("entity_policy_details");

		model.addObject("entity", entity);
		model.addObject("entityPolicies", retrieveAccessPolicyService.getPublicEntityPolicy(entity));

		return model;
	}

	/**
	 * Muestra el formulario de carga de ficheros de políticas de acceso.
	 * 
	 * @return el nombre del formulario de carga de ficheros de políticas de acceso.
	 */
	@RequestMapping(value = "/accessPolicyUpload.show", method = { RequestMethod.GET })
	public String displayForm() {
		return "access_policy_upload";
	}

	/**
	 * Carga el contenido de los ficheros seleccionados en el formulario.
	 * 
	 * @param uploadPolicyFiles Conjunto de ficheros a cargar.
	 * @param map Estructura con los nombres de los ficheros cargados con éxito.
	 * 
	 * @return el nombre del formulario que mostrará los ficheros cargados con éxito.
	 */
	@RequestMapping(value = "/accessPolicyUpload.save", method = { RequestMethod.POST })
	public String accessPolicyUpload(@ModelAttribute("uploadPolicyFiles") UploadPolicyFilesDto uploadPolicyFiles, Model map) {

		List<MultipartFile> files = uploadPolicyFiles.getFiles();

		List<String> filesOK = new ArrayList<String>();
		List<String> filesKO = new ArrayList<String>();
		List<String> filesErrors = new ArrayList<String>();

		if (null != files && files.size() > 0) {

			for (MultipartFile multipartFile : files) {

				if (!multipartFile.isEmpty()) {

					String fileName = multipartFile.getOriginalFilename();

					try {

						loadAccessPolicyService.loadAccessPolicy(multipartFile.getInputStream());

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

		return "access_policy_upload_success";
	}

	/**
	 * Carga las políticas de acceso por defecto, contenidas en <dataservices.home>.
	 *  
	 * @return la vista del índice general.
	 */
	@RequestMapping(value = "/loadDefault", method = { RequestMethod.GET, RequestMethod.POST })
	public String loadDefault(Model map) {

		try {
			loadAccessPolicyService.loadDefaultAccessPolicy();
		} catch (ServerException e) {

			map.addAttribute("errorCode", e.getErrorCode());
			map.addAttribute("message", e.getMessage());

			return "error_pages/server_error";
		}

		return "index";
	}

	/**
	 * Elimina cualquier acceso.
	 *  
	 * @return la vista del índice general.
	 */
	@RequestMapping(value = "/denyAll", method = { RequestMethod.GET, RequestMethod.POST })
	public String denyAll() {

		loadAccessPolicyService.resetAccessPolicies();

		return "index";
	}
}
