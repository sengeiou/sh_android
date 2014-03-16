package com.fav24.dataservices.domain.datasource;



public class DataSourceConfiguration
{
	private String name;
	private ConnectionPoolConfiguration connectionPoolConfiguration;
	private String url;
	private String userName;
	private String password;


	/**
	 * Constructor sin parámetros.
	 */
	public DataSourceConfiguration() {

		this.connectionPoolConfiguration = null;
		this.url = null;
		this.userName = null;
		this.password = null;
	}

	/**
	 * Retorna el nombre de la fuente de datos.
	 * 
	 * @return el nombre de la fuente de datos.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre de la fuente de datos.
	 * 
	 * @param name El nombre de la fuente de datos.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 * 
	 * @return la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 */
	public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
		return connectionPoolConfiguration;
	}

	/**
	 * Asigna la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 * 
	 * @param connectionPoolConfiguration Configuración del pool a asignar.
	 */
	public void setConnectionPoolConfiguration(ConnectionPoolConfiguration connectionPoolConfiguration) {
		this.connectionPoolConfiguration = connectionPoolConfiguration;
	}

	/**
	 * Retorna la casdena de conexión a la base de datos.
	 * 
	 * @return la casdena de conexión a la base de datos.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Asigna la casdena de conexión a la base de datos.
	 * 
	 * @param url La cadena de conexión a la base de datos.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Retorna el usuario de conexión a la base de datos.
	 * 
	 * @return el usuario de conexión a la base de datos.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Asigna el usuario de conexión a la base de datos.
	 * 
	 * @param userName El usuario de conexión a la base de datos.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Retorna la contraseña de conexión a la base de datos.
	 * 
	 * @return la contraseña de conexión a la base de datos.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Asigna la contraseña de conexión a la base de datos.
	 * 
	 * @param password La contraseña de conexión a la base de datos.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
