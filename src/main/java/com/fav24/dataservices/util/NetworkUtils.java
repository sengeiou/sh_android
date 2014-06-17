package com.fav24.dataservices.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Clase de utilidades de conectividad.
 */
public class NetworkUtils {

	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static Pattern IPADDRESS_COMPILED_PATTERN = Pattern.compile(IPADDRESS_PATTERN);


	/**
	 * Retorna true o false en función de si la ip indicada por parámetro equivale o no a un ip válida.
	 * 
	 * @param ip Dirección a validar.
	 * 
	 * @return true o false en función de si la ip indicada por parámetro equivale o no a un ip válida.
	 */
	public static boolean validateIPAddress(String ip) {

		Matcher matcher = IPADDRESS_COMPILED_PATTERN.matcher(ip);

		return matcher.matches();	  
	}

	/**
	 * Retorna una lista de arrays de cadenas de texto con información de las interfaces de red disponibles:
	 * 
	 *  Display name, Name, IPs.
	 * 
	 * @return una lista de arrays de cadenas de texto con información de las interfaces de red disponibles:
	 */
	public static AbstractList<String[]> getNetworkIterfaces() {

		AbstractList<String[]> networkIterfaces = new ArrayList<String[]>();

		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

			for (NetworkInterface netint : Collections.list(nets)) {

				Collection<InetAddress> inetAddresses = Collections.list(netint.getInetAddresses());

				String networkInt[] = new String[inetAddresses.size() + 2];
				networkInt[0] = netint.getDisplayName();
				networkInt[1] = netint.getName();

				int i=2;
				for (InetAddress inetAddress : inetAddresses) {
					networkInt[i] = inetAddress.getHostAddress();
				}

				networkIterfaces.add(networkInt);
			}
		} catch (SocketException e) {
			return null;
		}

		return networkIterfaces;
	}

	/**
	 * Retorna una cadena de texto con la dirección IP interna de la máquina.
	 * 
	 * @return una cadena de texto con la dirección IP interna de la máquina.
	 */
	public static String getDefaultInternalIP() {

		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * Retorna la IP de acceso externo.
	 * 
	 * @return la IP de acceso externo.
	 */
	public static String getExternalIP() {

		URL externalIPService;
		BufferedReader in = null;
		String ip;

		try {
			externalIPService = new URL("http://ifconfig.me/ip");

			in = new BufferedReader(new InputStreamReader(externalIPService.openStream()));

			while((ip = in.readLine()) != null) {

				if (validateIPAddress(ip)) {
					return ip;
				}
			}

		} catch (IOException e) {

			return null;
		}
		finally {
			IOUtils.closeQuietly(in);
		}


		return ip;
	}
}
