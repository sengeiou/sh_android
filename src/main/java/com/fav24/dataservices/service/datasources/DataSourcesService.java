package com.fav24.dataservices.service.datasources;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio Datasources. 
 * 
 * @author Fav24
 */
public interface DataSourcesService {

	public static final Logger logger = LoggerFactory.getLogger(DataSourcesService.class);

	public static final String ERROR_DATASOURCE_GET_INFO_FAILED = "G000";
	public static final String ERROR_DATASOURCE_GET_INFO_FAILED_MESSAGE = "Error al obtener la información de la fuente de datos.";
	public static final String ERROR_STATS_DATASOURCE_GET_INFO_FAILED = "G001";
	public static final String ERROR_STATS_DATASOURCE_GET_INFO_FAILED_MESSAGE = "Error al obtener la información de la fuente de datos de uso.";
	public static final String ERROR_DATASOURCE_GET_TIMESTAMP_FAILED = "G002";
	public static final String ERROR_DATASOURCE_GET_TIMESTAMP_FAILED_MESSAGE = "Error al obtener la fecha y hora de la fuente de datos.";
	public static final String ERROR_STATS_DATASOURCE_GET_TIMESTAMP_FAILED = "G003";
	public static final String ERROR_STATS_DATASOURCE_GET_TIMESTAMP_FAILED_MESSAGE = "Error al obtener la fecha y hora de la fuente de datos de uso.";


	/**
	 * Retorna un mapa de atributos con la información de la fuente de datos a publicar.
	 * 
	 * @return un mapa de atributos con la información de la fuente de datos a publicar.
	 * 
	 * @throws ServerException 
	 */
	public Map<String, String> getDataServiceDataSourceInformation() throws ServerException;
	
	/**
	 * Retorna la fecha y hora del subsistema en milisegundos desde epoch.
	 * 
	 * @return la fecha y hora del subsistema en milisegundos desde epoch.
	 * 
	 * @throws ServerException 
	 */
	public Long getDataServiceDataSourceTime() throws ServerException;

	/**
	 * Retorna un mapa de atributos con la información de la fuente de datos de uso.
	 * 
	 * @return un mapa de atributos con la información de la fuente de datos de uso.
	 * 
	 * @throws ServerException 
	 */
	public Map<String, String> getStatisticsDataSourceInformation() throws ServerException;
	
	/**
	 * Retorna la fecha y hora del subsistema estadístico en milisegundos desde epoch.
	 * 
	 * @return la fecha y hora del subsistema estadístico en milisegundos desde epoch.
	 * 
	 * @throws ServerException 
	 */
	public Long getStatisticsDataSourceTime() throws ServerException;
}
