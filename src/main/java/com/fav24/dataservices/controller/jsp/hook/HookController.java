package com.fav24.dataservices.controller.jsp.hook;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.controller.jsp.BaseJspController;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.dto.UploadFilesDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.hook.HookConfigurationService;
import com.fav24.dataservices.service.hook.HookService;
import com.fav24.dataservices.util.FileUtils;

/**
 * Controla las peticiones de entrada a las páginas de gestión de la seguridad.
 */
@Scope("singleton")
@Controller
@RequestMapping("/hook")
public class HookController extends BaseJspController {

	@Autowired
	protected HookConfigurationService hookConfigurationService;
	private File hookSourcesTempDir;


	/**
	 * Muestra la lista de hooks disponibles para este servicio de datos.
	 *  
	 * @return el modelo y la vista, con la lista de hooks disponibles para este servicio de datos.
	 */
	@RequestMapping(value = "/availableHooks", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView availableEntities() {

		ModelAndView model = new ModelAndView("available_hooks");

		model.addObject("hooks", hookConfigurationService.getAvailableHooks());

		return model;
	}

	/**
	 * Muestra el detalle de un hook.
	 * 
	 * @param hook Hook del que mostrará su detalle.
	 * 
	 * @return el modelo y la vista, con el el detalle de un hook.
	 */
	@RequestMapping(value = "/hookDetails", method = { RequestMethod.GET })
	public ModelAndView entityPolicies(@ModelAttribute(value="hook") String hook) {

		ModelAndView model = new ModelAndView("hook_details");

		model.addObject("alias", hook);
		model.addObject("hook", hookConfigurationService.getHook(hook));

		return model;
	}

	/**
	 * Muestra el formulario de carga de ficheros de puntos de inserción.
	 * 
	 * @return el nombre del formulario de carga de ficheros de puntos de inserción.
	 */
	@RequestMapping(value = "/hookUpload.show", method = { RequestMethod.GET, RequestMethod.POST })
	public String displayForm() {
		return "hook_upload";
	}

	/**
	 * Carga el contenido de los ficheros seleccionados en el formulario.
	 * 
	 * @param uploadHookFiles Conjunto de ficheros a cargar.
	 * @param map Estructura con los nombres de los ficheros cargados con éxito.
	 * 
	 * @return el nombre del formulario que mostrará los ficheros cargados con éxito.
	 */
	@RequestMapping(value = "/hookUpload.save", method = { RequestMethod.POST })
	public String accessPolicyUpload(@ModelAttribute("uploadHookFiles") UploadFilesDto uploadHookFiles, Model map) {

		List<MultipartFile> files = uploadHookFiles.getFiles();
		List<Boolean> filesAsDefault = uploadHookFiles.getFilesAsDefault();

		List<String> filesOK = new ArrayList<String>();
		List<String> filesKO = new ArrayList<String>();
		List<String> filesErrors = new ArrayList<String>();

		if (null != files && files.size() > 0) {

			int i = 0;
			for (MultipartFile multipartFile : files) {

				if (!multipartFile.isEmpty()) {

					String fileName = multipartFile.getOriginalFilename();
					Boolean fileAsDefault = filesAsDefault == null ? false : filesAsDefault.get(i++);

					try {

						RemoteFiles remoteFiles;

						if (fileAsDefault != null && fileAsDefault) {

							remoteFiles = new RemoteFiles(new File[]{FileUtils.createOrReplaceExistingFile(multipartFile)});
						}
						else {

							if (hookSourcesTempDir == null) {

								Path path = Files.createTempDirectory("hooks-sources.");
								hookSourcesTempDir = path.toFile();
							}

							File source = new File(hookSourcesTempDir.toString() + File.separator + fileName);
							multipartFile.transferTo(source);

							remoteFiles = new RemoteFiles(new File[]{source});
						}

						Map<String, StringBuilder> diagnostics = hookConfigurationService.loadHooks(remoteFiles);

						for(Entry<String, StringBuilder> diagnostic : diagnostics.entrySet()) {

							if (diagnostic.getValue() == null) {
								filesOK.add(fileName);
							}
							else {
								filesKO.add(remoteFiles.getURLs()[0].getFile());
								filesErrors.add(diagnostic.getValue().toString());
							}
						}

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

		return "hook_uploaded";
	}

	/**
	 * Carga los hooks por defecto, contenidos en <dataservices.home>.
	 * 
	 * @param map Estructura con los atributos del estado de la operación.
	 * 
	 * @return la vista del resultado de la carga.
	 * 
	 * @throws ServerException
	 */
	@RequestMapping(value = "/loadDefault", method = { RequestMethod.GET, RequestMethod.POST })
	public String loadDefault(Model map) throws ServerException {

		Map<String, StringBuilder> diagnostics = hookConfigurationService.loadDefaultHooks();

		if (diagnostics == null || diagnostics.isEmpty()) {
			map.addAttribute("title", "Carga de los puntos de inserción (hooks) por defecto.");
			map.addAttribute("message", "Los hooks han sido cargados con éxito.");
		}
		else {

			for(Entry<String, StringBuilder> diagnostic : diagnostics.entrySet()) {

				if (diagnostic.getValue() != null) {

					throw new ServerException(HookService.ERROR_HOOK_COMPILATION_ERRORS_FOUND, 
							String.format(HookService.ERROR_HOOK_COMPILATION_ERRORS_FOUND_MESSAGE, diagnostic.getKey()), 
							diagnostic.getValue().toString());
				}
			}
		}

		return "error_pages/server_success";
	}

	/**
	 * Elimina todos los puntos de inserción disponibles.
	 *  
	 * @param map Estructura con los atributos del estado de la operación.
	 *  
	 * @return la vista del índice general.
	 * 
	 * @throws ServerException
	 */
	@RequestMapping(value = "/dropAll", method = { RequestMethod.GET, RequestMethod.POST })
	public String denyAll(Model map) throws ServerException {

		hookConfigurationService.dropHooks();

		map.addAttribute("title", "Puntos de inserción descargados.");
		map.addAttribute("message", "Todos los puntos de inserción (hooks) han sido descargados. Todo acceso a entidades con políticas dependientes de algún hook, será rechazado con error.");

		return "error_pages/server_success";
	}
}
