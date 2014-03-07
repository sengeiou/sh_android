package com.fav24.dataservices.service;

import java.util.AbstractList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSampleData;


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
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 y 24 horas.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public AbstractList<MonitorSampleData> getSystemMemoryStatus(Long period, Long timeRange);
	
	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 y 24 horas.
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, AbstractList<MonitorSampleData>> getSystemStorageStatus(Long period, Long timeRange);
}
