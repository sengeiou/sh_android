package com.fav24.dataservices.xml.cache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Clase para convertir de notación resumida a bytes, y viceversa.
 * 
 *  Bytes nomenclature:
 *	# -> bytes. # bytes
 *	#K -> Kilobytes. (# x 1024) bytes
 *	#M -> Megabytes. (# x 1000 x 1024) bytes
 *	#G -> Gigabytes. (# x 1000000 x 1024) bytes
 */
public class StorageSize {

	public static final long KILO = 1024;
	public static final long MEGA = 1000 * KILO;
	public static final long GIGA = 1000 * MEGA;
	public static final String TAG_KILO = "K";
	public static final String TAG_MEGA = "M";
	public static final String TAG_GIGA = "G";

	public static final String STORAGE_PATTERN = "([0-9]{1,19})((G|M|K)|)";
	public static final Pattern StoragePattern = Pattern.compile(STORAGE_PATTERN);


	/**
	 * A partir de una cadena de texto con el patrón {@linkplain #STORAGE_PATTERN}, 
	 * retorna su representación en bytes.
	 * 
	 * @param size Tamaño en forma de texto.
	 * 
	 * @return tamaño en bytes.
	 */
	public static Long fromStringToBytes(String size) {

		if (size == null) {
			return null;
		}

		Matcher matcher = StoragePattern.matcher(size);

		if (matcher.find()) {

			String digit = matcher.group(1);
			String sufix = matcher.group(2);

			Long bytes = Long.parseLong(digit);

			if (TAG_GIGA.equals(sufix)) {

				bytes *= GIGA;
			}
			else if (TAG_MEGA.equals(sufix)) {

				bytes *= MEGA;
			}
			else if (TAG_KILO.equals(sufix)) {

				bytes *= KILO;
			}

			return bytes;
		}

		return null;
	}

	/**
	 * Retorna una versión simplificada en texto del tamaño en bytes indicado.
	 *
	 * Nota: En caso de no ser divisible por ningún orden de magnitud, se retornará en bytes
	 * independientemente de su tamaño.
	 * 
	 * @param size Tamaño en bytes a convertir.
	 * 
	 * @return una versión simplificada en texto del tamaño en bytes indicado.
	 */
	public static String fromBytesToString(Long size) {

		if (size == null) {

			return null;
		}

		if (size < KILO) {
			return size.toString();
		}
		else {

			if (size < MEGA) {
				if (size % KILO == 0) {
					return Long.toString(size / KILO) + TAG_KILO;
				}
				else {
					return size.toString();
				}
			}
			else if (size < GIGA) {
				if (size % MEGA == 0) {
					return Long.toString(size / MEGA) + TAG_MEGA;
				}
				else {
					return size.toString();
				}
			}
			else {
				if (size % GIGA == 0) {
					return Long.toString(size / GIGA) + TAG_GIGA;
				}
				else {
					return size.toString();
				}
			}
		}
	}
}
