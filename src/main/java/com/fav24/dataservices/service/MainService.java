package com.fav24.dataservices.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
public interface MainService {

	public static final Logger logger = LoggerFactory.getLogger(MainService.class);

	public static final String ERROR_DATASOURCE_GET_INFO_FAILED = "G000";
	public static final String ERROR_DATASOURCE_GET_INFO_FAILED_MESSAGE = "Error al obtener la información de la fuente de datos.";
	public static final String ERROR_STATS_DATASOURCE_GET_INFO_FAILED = "G001";
	public static final String ERROR_STATS_DATASOURCE_GET_INFO_FAILED_MESSAGE = "Error al obtener la información de la fuente de datos de uso.";

	public static final String MAX_MEMORY = "MaxMemory"; //Memória máxima a usar. Se configura mediante el parámetro - Xmx
	public static final String TOTAL_MEMORY = "TotalMemory"; //Hasta donde se puede expandir el heap. Se configura mediante el parámetro -Xms
	public static final String FREE_MEMORY = "FreeMemory"; //Memoria disponible en el sistema. 
	public static final String USED_MEMORY = "UsedMemory"; //Memoria en uso. 

	public static final String TOTAL_STORAGE_SPACE = "TotalStorageSpace"; //Capacidad total de almacenamiento.
	public static final String TOTAL_USABLE_STORAGE_SPACE = "TotalUsableStorageSpace"; //Espacio disponible de almacenamiento.

	/**
	 * Retorna un mapa de atributos con la información de la fuente de datos a publicar.
	 * 
	 * @return un mapa de atributos con la información de la fuente de datos a publicar.
	 * 
	 * @throws ServerException 
	 */
	public Map<String, String> getDataSourceInformation() throws ServerException;

	/**
	 * Retorna un mapa de atributos con la información de la fuente de datos de uso.
	 * 
	 * @return un mapa de atributos con la información de la fuente de datos de uso.
	 * 
	 * @throws ServerException 
	 */
	public Map<String, String> getStatsDataSourceInformation() throws ServerException;
	
	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public Map<String, Long> getSystemMemoryStatus();
	
	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * {@linkplain #TOTAL_STORAGE_SPACE}
	 * {@linkplain #TOTAL_USABLE_STORAGE_SPACE}
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, Map<String, Long>> getSystemStorageStatus();
}
