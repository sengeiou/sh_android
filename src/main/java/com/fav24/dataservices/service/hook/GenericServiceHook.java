package com.fav24.dataservices.service.hook;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;

/**
 * Puntos de incorporación al ciclo de vido de una petición al servicio de datos genérico.
 * 
 * Todas las classes que implementen esta interfaz, serán cargadas por el servidor, y estarán disponibles mientras este esté en servicio.
 * 
 * @param <T> Tipo de conexión.
 */
public interface GenericServiceHook {

	/**
	 * Enumeración de los resultados posibles de una llamada a un método de un hook. 
	 */
	public static enum HookMethodOutput {
		
		/** Continua con la ejecución de la petición normalmente, después de ejecutar el método del "hook".*/
		CONTINUE("Continue"),
		/** Detiene la ejecución de la petición con éxito, después de ejecutar el método del "hook".*/
		STOP_OK("StopOk"),
		/** Detiene la ejecución de la petición con error, después de ejecutar el método del "hook".*/
		STOP_KO("StopKo");

		private final String hookMethodOutput;

		/**
		 * Constructor privado del tipo de resultado.
		 * 
		 * @param hookMethodOutput Cadena de texto aue identifica la acción a realizar al terminar la ejecución de un método de un hook.
		 */
		HookMethodOutput(String hookMethodOutput) {

			this.hookMethodOutput = hookMethodOutput;
		}

		/**
		 * Retorna la cadena de texto aue identifica la acción a realizar al terminar la ejecución de un método de un hook.
		 * 
		 * @return la cadena de texto aue identifica la acción a realizar al terminar la ejecución de un método de un hook.
		 */
		public String getHookMethodOutput() {

			return hookMethodOutput;
		}

		/**
		 * Retorna la acción a realizar al terminar la ejecución de un método de un hook a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el tipo de acción.
		 * 
		 * @return la acción a realizar al terminar la ejecución de un método de un hook a partir de la cadena de texto indicada.
		 */
		public static HookMethodOutput fromString(String text) {

			if (text != null) {

				for (HookMethodOutput hookMethodOutput : HookMethodOutput.values()) {

					if (text.equalsIgnoreCase(hookMethodOutput.hookMethodOutput)) {
						return hookMethodOutput;
					}
				}
			}
			return null;
		}
	}

	
	/**
	 * Retorna una cadena de texto que identifica este hook.
	 * 
	 * Nota: si durante el proceso de carga se encuentra más de un hook con el mismo alias,
	 * prevalece el último en ser cargado. El proceso de carga se realiza en por nombre de archivo en órden ascendente. 
	 * 
	 * @return una cadena de texto que identifica este hook.
	 */
	public String getAlias();
	
	/**
	 * Retorna una cadena de texto con una descripción funcional del hook.
	 * 
	 * @return una cadena de texto con una descripción funcional del hook.
	 */
	public String getDescription();

	/**
	 * Método que se instancia antes de ejecutar el bloque de operaciones de una petición.
	 * 
	 * @param connection Conexión abierta al subsistema.
	 * @param accessPolicy Política general de acceso a datos.
	 * @param generic Estructura completa de la petición.
	 * 
	 * @return acción a realizar al terminar la ejecución de este método. 
	 */
	public <T> HookMethodOutput requestBegin(T connection, AccessPolicy accessPolicy, Generic generic);

	/**
	 * Método que se instancia depués de ejecutar el bloque de operaciones de una petición.
	 * 
	 * @param connection Conexión abierta al subsistema.
	 * @param accessPolicy Política general de acceso a datos.
	 * @param generic Estructura completa de la petición.
	 * 
	 * @return acción a realizar al terminar la ejecución de este método. 
	 */
	public <T> HookMethodOutput requestEnd(T connection, AccessPolicy accessPolicy, Generic generic);

	/**
	 * Método que se instancia antes de ejecutar cada operación del bloque de operaciones de una petición.
	 * 
	 * @param connection Conexión abierta al subsistema.
	 * @param entityAccessPolicy Política de acceso de la entidad sobre la que se ejecutará la operación
	 * @param operation Estructura de la operación que se ejecutará a continuación.
	 * 
	 * @return acción a realizar al terminar la ejecución de este método. 
	 */
	public <T> HookMethodOutput operationBegin(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation);

	/**
	 * Método que se instancia antes despues de ejecutar cada operación del bloque de operaciones de una petición.
	 * 
	 * @param connection Conexión abierta al subsistema.
	 * @param entityAccessPolicy Política de acceso de la entidad sobre la que se ejecutará la operación
	 * @param operation Estructura de la operación que se ejecutará a continuación.
	 * 
	 * @return acción a realizar al terminar la ejecución de este método. 
	 */
	public <T> HookMethodOutput operationEnd(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation);
}
