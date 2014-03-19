package com.fav24.dataservices.service.system;

import java.util.AbstractList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample;


/**
 * Interfaz de servicio System. 
 * 
 * @author Fav24
 */
public interface SystemService {

	public static final Logger logger = LoggerFactory.getLogger(SystemService.class);


	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual
	 * en este mismo instante.
	 * 
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public MonitorSample getSystemMemoryStatus();
	
	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 segundo a 24 horas.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long period, Long timeRange);
	
	/**
	 * Retorna la información del estado de la carga de proceso del sistema
	 * en este mismo instante.
	 * 
	 * @return la información del estado de la carga de proceso del sistema.
	 */
	public MonitorSample getSystemCpuActivity();
	
	/**
	 * Retorna la información del estado de la carga de proceso del sistema.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 segundo a 24 horas.
	 *  
	 * @return la información del estado de la carga de proceso del sistema.
	 */
	public AbstractList<MonitorSample> getSystemCpuActivity(Long period, Long timeRange);
	
	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada
	 * en este mismo instante.
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, MonitorSample> getSystemStorageStatus();
	
	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 segundo y 24 horas.
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, AbstractList<MonitorSample>> getSystemStorageStatus(Long period, Long timeRange);
}
