package com.fav24.dataservices.service.system;

import java.util.AbstractList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.MonitorSample;
import com.fav24.dataservices.monitoring.meter.WorkloadMeter;


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
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long offset, Long timeRange, Long period) throws ServerException;

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
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * @return la información del estado de la carga de proceso del sistema.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemCpuActivity(Long offset, Long timeRange, Long period) throws ServerException;

	/**
	 * Retorna el medidor de trabajo realizado por el sistema.
	 * 
	 * @return el medidor de trabajo realizado por el sistema.
	 */
	public WorkloadMeter getWorkloadMeter();

	/**
	 * Retorna la información del trabajo realizado por el sistema
	 * en este mismo instante.
	 * 
	 * @return la información del trabajo realizado por el sistema.
	 */
	public MonitorSample getSystemWorkload();

	/**
	 * Retorna la información del trabajo realizado por el sistema
	 * 
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * @return la información del trabajo realizado por el sistema
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemWorkload(Long offset, Long timeRange, Long period) throws ServerException;
	
	/**
	 * Retorna la información asociada al elemento de almacenamiento en este mismo instante.
	 * 
	 * @return la información asociada al elemento de almacenamiento en este mismo instante.
	 */
	public MonitorSample getSystemStorageStatus(String storeName);

	/**
	 * Retorna la información asociada al elemento de almacenamiento en cuanto a:
	 * 
	 * 	{@linkplain #StorageMeter.TOTAL_STORAGE_SPACE}: Capacidad total de almacenamiento.
	 * 	{@linkplain #StorageMeter.TOTAL_USABLE_STORAGE_SPACE}: Espacio disponible de almacenamiento.
	 * 
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 * 
	 * @return la información asociada al elemento de almacenamiento.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemStorageStatus(String storeName, Long offset, Long timeRange, Long period) throws ServerException;
}
