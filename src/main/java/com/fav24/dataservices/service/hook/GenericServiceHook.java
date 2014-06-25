package com.fav24.dataservices.service.hook;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;

/**
 * Puntos de incorporación al ciclo de vido de una petición al servicio de datos genérico.
 * 
 * Todas las classes que implementen esta interfaz, serán cargadas por el servidor, y estarán disponibles mientras este esté en servicio.
 * 
 * @param <T> Tipo de conexión.
 */
public interface GenericServiceHook {

	/**
	 * Estructura de salida de una llamada a un método de un hook. 
	 */
	public static class HookMethodOutput {

		// Salida por defecto en caso de ejecución exitosa de un hook.
		public static final HookMethodOutput CONTINUE = new HookMethodOutput(HookMethodOutputCode.CONTINUE, "");
		// Salida por defecto en caso de ejecución exitosa de un hook pero que detiene la ejecución del resto de operaciones.
		public static final HookMethodOutput STOP_OK = new HookMethodOutput(HookMethodOutputCode.STOP_OK, "");

		private HookMethodOutputCode code;
		private String message;


		/**
		 * Constructor de la estructura de resultado STOP_KO.
		 * 
		 * @param message Mensaje asociado a la acción de parada con error.
		 */
		public HookMethodOutput(String message) {

			this(HookMethodOutputCode.STOP_KO, message);
		}

		/**
		 * Constructor de la estructura de resultado.
		 * 
		 * @param code Código de la acción a realizar al terminar la ejecución de un método de un hook.
		 * @param message Mensaje asociado a la acción.
		 */
		public HookMethodOutput(HookMethodOutputCode code, String message) {

			this.code = code;
			this.message = message;
		}

		/**
		 * Retorna el código enumerado identifica la acción a realizar al terminar la ejecución de un método de un hook.
		 * 
		 * @return el código enumerado identifica la acción a realizar al terminar la ejecución de un método de un hook.
		 */
		public HookMethodOutputCode getCode() {

			return code;
		}

		/**
		 * Retorna el texto que describe el fin de la ejecución de un método de un hook.
		 * 
		 * Nota: Este texto, es el que se mostrará en caso de código STOP_KO.
		 * 
		 * @return el texto que describe el fin de la ejecución de un método de un hook.
		 */
		public String getMessage() {

			return message;
		}
	}

	/**
	 * Enumeración de los tipos de resultados posibles de una llamada a un método de un hook. 
	 */
	public static enum HookMethodOutputCode {

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
		HookMethodOutputCode(String hookMethodOutput) {

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
		public static HookMethodOutputCode fromString(String text) {

			if (text != null) {

				for (HookMethodOutputCode hookMethodOutput : HookMethodOutputCode.values()) {

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
