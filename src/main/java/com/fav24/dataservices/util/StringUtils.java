package com.fav24.dataservices.util;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;


/**
 * Utilidades para cadenas de texto.
 */
public class StringUtils {


	/**
	 * Genera un texto aleatorio de la longitud indicada
	 *
	 * @param length Especifica la longitud del texto aleatorio. Se ha de cumplir ( 0 < length )
	 * @return texto aleatorio de la longitud indicada
	 */
	public static String randomString(int length) {

		Date date = new Date();
		String seed = "nkoads9023nds9";
		String randomDate = new Timestamp(date.getTime()).toString();
		String randomText = "";
		while (randomText.length() < length) {
			randomText += StringUtils.encodeMd5(randomDate + seed);
		}
		return randomText.substring(0, length - 1);
	}

	/**
	 * Retorna el código MD5 para la cadena suministrada por parámetro.
	 * 
	 * @param text Cadena de la que se desea obtener el MD5.
	 * 
	 * @return el código MD5 para la cadena suministrada por parámetro.
	 */
	public static String encodeMd5(String text) {
		return StringUtils.ApplyHash(text, "md5");
	}

	/**
	 * Retorna el código SHA-1 para la cadena suministrada por parámetro.
	 * 
	 * @param text Cadena de la que se desea obtener el SHA-1.
	 * 
	 * @return el código SHA-1 para la cadena suministrada por parámetro.
	 */
	public static String encodeSha1(String text) {
		return StringUtils.ApplyHash(text, "SHA-1");
	}

	/**
	 * Retorna la cadena suministrada por parámetro en Base 64.
	 * 
	 * @param text Cadena de la que se desea obtener su versión en Base64.
	 * 
	 * @return la cadena suministrada por parámetro en Base 64.
	 */
	public static String encodeBase64(String text) {
		byte[] encodedBytes = Base64.encodeBase64(text.getBytes());
		return new String(encodedBytes);
	}

	/**
	 * Aplica el algoritmo de hash indicado al texto
	 *
	 * @param text      Texto al que se quiere aplicar el algoritmo de hash
	 * @param algorithm Algoritmo de Hash a aplicar ( MD5 o SHA-1 )
	 * @return Texto con md5 aplicado
	 */
	private static String ApplyHash(String text, String algorithm) {
		// MD5
		// SHA-1
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
			byte[] array = md.digest(text.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			return null;
		}

	}

	// Encondig stuff

	/**
	 * Transforma un String codificado en ISO-8859-1 a UTF-8
	 *
	 * @param text String codificado en ISO-8859-1
	 * @return String codificado en UTF-8
	 * @throws UnsupportedEncodingException
	 */
	public static String fixEncondig(String text) throws UnsupportedEncodingException {
		if (text == null) return null;
		byte[] bytes = text.getBytes("ISO-8859-1");
		String res = (new String(bytes, "UTF-8"));
		return res;
	}

	/**
	 * Transforma un String codificado en UTF-8 a ISO-8859-1
	 *
	 * @param text String codificado en UTF-8
	 * @return String codificado en ISO-8859-1
	 * @throws UnsupportedEncodingException
	 */
	public static String reverseEncondig(String text) throws UnsupportedEncodingException {
		if (text == null) return null;
		byte[] bytes = text.getBytes("UTF-8");
		String res = (new String(bytes, "ISO-8859-1"));
		return res;
	}
}