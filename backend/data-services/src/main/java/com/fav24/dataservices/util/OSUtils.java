package com.fav24.dataservices.util;

public class OSUtils {

	/**
	 * Tipos de sistemas operativos
	 */
	public enum OSType {

		MacOS("mac", "darwin"),
		Windows("win"),
		Linux("nux"),
		Other("generic");

		private static OSType detectedOS;

		private final String[] keys;

		/**
		 * Constructor privado para la creación de los enumerados.
		 * 
		 * @param keys Conjunto de claves que identifican total o parcialmente a un cierto sistema operativo.
		 */
		private OSType(String... keys) {
			this.keys = keys;
		}

		/**
		 * Retorma true o false en función de si la clave sumistrada coincide total o parcialmente con el nombre sistema operativo.
		 * 
		 * @param osKey Clave a comprobar.
		 * 
		 * @return true o false en función de si la clave sumistrada coincide total o parcialmente con el nombre sistema operativo.
		 */
		private boolean match(String osKey) {

			for (int i = 0; i < keys.length; i++) {

				if (osKey.indexOf(keys[i]) != -1) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Retorna el tipo de sistema operativo.
		 *
		 * @return el tipo de sistema operativo.
		 */
		public static OSType getOSType() {

			if (detectedOS == null) {
				detectedOS = getOperatingSystemType(System.getProperty("os.name", Other.keys[0]).toLowerCase());
			}

			return detectedOS;
		}

		/**
		 * Obtiene el tipo de sistema operativo.
		 * 
		 * @param osKey clave a comprobar.
		 * 
		 * @return el tipo de sistema operativo.
		 */
		private static OSType getOperatingSystemType(String osKey) {

			for (OSType osType : values()) {

				if (osType.match(osKey)) {
					return osType;
				}
			}

			return Other;
		}
	}
}