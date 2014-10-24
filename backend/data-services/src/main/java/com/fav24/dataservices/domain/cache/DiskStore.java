package com.fav24.dataservices.domain.cache;


/**
 * Clase que almacena la ubicació de los ficheros de persistencia de las cachés,
 * para un cierto gestor.
 */
public class DiskStore
{
	public String DEFAULT_SYSTEM_TEMP_DIR = "java.io.tmpdir";

	private String path;


	/**
	 * Constructor por defecto.
	 */
	public DiskStore() {
		this.path = DEFAULT_SYSTEM_TEMP_DIR;
	}

	/**
	 * Constructor con parámetro.
	 * 
	 * @param path Ubicación de los ficheros de persistencia de la caché. 
	 */
	public DiskStore(String path) {

		this.path = path;
	}

	/**
	 * Retorna la ubicación para los ficheros de caché.
	 * 
	 * @return la ubicación para los ficheros de caché.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Asigna la ubicación para los ficheros de caché.
	 * 
	 * @param path La nueva ubicación.
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @return la nueva instancia.
	 */
	public DiskStore clone() {
		
		DiskStore clone = new DiskStore(path);
		
		return clone;
	}
}
