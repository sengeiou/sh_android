package com.fav24.dataservices.service.hook;



/**
 * Interfaz para servicios relacionados con los puntos de inserción (hooks). 
 */
public interface HookService {

	public static final String ERROR_HOOK_SOURCE_ACCESS = "HK001";
	public static final String ERROR_HOOK_SOURCE_ACCESS_MESSAGE = "No ha sido posible acceder normalmente a alguno de los fuentes de los hooks: <%s>.";
	public static final String ERROR_HOOK_COMPILATION_ERRORS_FOUND = "HK002";
	public static final String ERROR_HOOK_COMPILATION_ERRORS_FOUND_MESSAGE = "Se han encotrado errores de compilación para el hook <%s>.";
	public static final String ERROR_HOOK_CLASS_NOT_FOUND = "HK003";
	public static final String ERROR_HOOK_CLASS_NOT_FOUND_MESSAGE = "No ha sido posible localizar la clase <%s>.";
	public static final String ERROR_HOOK_CLASS_INSTANCE = "HK004";
	public static final String ERROR_HOOK_CLASS_INSTANCE_MESSAGE = "No ha sido posible crear una instancia de la clase <%s>.";
	public static final String ERROR_HOOK_DIAGNOSTIC = "HK005";
	public static final String ERROR_HOOK_DIAGNOSTIC_MESSAGE = "Se ha producido un problema durante la construcción del diagnóstico por parte del compilador para el fichero <%s>.";
	public static final String ERROR_INVALID_HOOK_FILE_URL = "HK006";
	public static final String ERROR_INVALID_HOOK_FILE_URL_MESSAGE = "La URL <%s> no se corresponde con ningún fichero de definición de hook.";
	public static final String ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD = "HK007";
	public static final String ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD_MESSAGE = "No existe ningún fichero de definición de hook por defecto.";
	public static final String ERROR_INVALID_HOOK_CLASSPATH_URL = "HK008";
	public static final String ERROR_INVALID_HOOK_CLASSPATH_URL_STRING = "La URL de las dependencias de los hooks <%s>, no es válida.";
	public static final String ERROR_INVALID_HOOK_DEPENDENCY_FILE_URL = "HK009";
	public static final String ERROR_INVALID_HOOK_DEPENDENCY_FILE_URL_MESSAGE = "La URL <%s> no se corresponde con ningún fichero de dependencia para hooks.";
	public static final String ERROR_HOOK_CLASS_INSTANCE_DEPENDENCY_NOT_FOUND = "HK010";
	public static final String ERROR_HOOK_CLASS_INSTANCE_DEPENDENCY_NOT_FOUND_MESSAGE = "No ha sido posible localizar dependencia <%s> para la clase <%s>.";
}
