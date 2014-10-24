package com.fav24.dataservices.dto.cache;

import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de una petición de consulta de
 * cachés.
 */
public class EntityCacheDto extends BaseDto {

	private static final long serialVersionUID = -8430309467185088456L;

	private String managerName;
	private String entityAlias;
	private Long maxBytesLocalHeap;
	private Long maxBytesLocalDisk;


	/**
	 * Constructor por defecto.
	 */
	public EntityCacheDto() {
		entityAlias = null;
		maxBytesLocalHeap = null;
		maxBytesLocalDisk = null;
	}

	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public EntityCacheDto(ServerException e) {
		super(e);
	}

	/**
	 * Retorna el nombre del gestor de cachés al que pertenece esta caché.
	 * 
	 * @return el nombre del gestor de cachés al que pertenece esta caché.
	 */
	public String getManagerName() {
		return managerName;
	}

	/**
	 * Asigna el nombre del gestor de cachés al que pertenece esta caché.
	 *  
	 * @param name Nombre a asignar.
	 */
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	
	/**
	 * Retorna el alias de la entidad a la que hace referencia esta caché.
	 * 
	 * @return el alias de la entidad a la que hace referencia esta caché.
	 */
	public String getEntityAlias() {
		return entityAlias;
	}

	/**
	 * Asigna el alias de la entidad a la que hace referencia esta caché.
	 *  
	 * @param entityAlias Alias a asignar.
	 */
	public void setEntityAlias(String entityAlias) {
		this.entityAlias = entityAlias;
	}

	/**
	 * Retorna el número máximo de bytes en memoria.
	 * 
	 * @return el número máximo de bytes en memoria.
	 */
	public Long getMaxBytesLocalHeap() {

		return maxBytesLocalHeap;
	}

	/**
	 * Asigna el número máximo de bytes en memoria.
	 * 
	 * @param maxBytesLocalHeap El número máximo de bytes.
	 */
	public void setMaxBytesLocalHeap(Long maxBytesLocalHeap) {
		this.maxBytesLocalHeap = maxBytesLocalHeap;
	}

	/**
	 * Retorna el número máximo de bytes en disco.
	 * 
	 * @return el número máximo de bytes en disco.
	 */
	public Long getMaxBytesLocalDisk() {

		return maxBytesLocalDisk;
	}

	/**
	 * Asigna el número máximo de bytes en disco.
	 * 
	 * @param maxBytesLocalDisk El número máximo de bytes.
	 */
	public void setMaxBytesLocalDisk(Long maxBytesLocalDisk) {
		this.maxBytesLocalDisk = maxBytesLocalDisk;
	}
}
