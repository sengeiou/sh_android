package com.fav24.dataservices.dto.cache;

import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de una petición de consulta de
 * gestor de cachés.
 */
public class EntityCacheManagerDto extends BaseDto {

	private static final long serialVersionUID = -8430309467185088456L;

	private String name;
	private Long maxBytesLocalHeap;
	private Long maxBytesLocalDisk;
	private EntityCacheDto[] caches;


	/**
	 * Constructor por defecto.
	 */
	public EntityCacheManagerDto() {
		name = null;
		caches = null;
	}

	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public EntityCacheManagerDto(ServerException e) {
		super(e);
	}

	/**
	 * Retorna el nombre del gestor de cachés.
	 * 
	 * @return el nombre del gestor de cachés.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre del gestor de cachés.
	 *  
	 * @param name Nombre a asignar.
	 */
	public void setName(String name) {
		this.name = name;
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

	/**
	 * Retorna el número máximo de bytes en memoria.
	 * 
	 * @return el número máximo de bytes en memoria.
	 */
	public EntityCacheDto[] getCaches() {

		return caches;
	}

	/**
	 * Asigna el número máximo de bytes en memoria.
	 * 
	 * @param maxBytesLocalHeap El número máximo de bytes.
	 */
	public void setCaches(EntityCacheDto[] caches) {
		this.caches = caches;
	}
}
