package com.fav24.dataservices.dto;



/**
 * Estructura de una acción sobre una entidad.
 * 
 * @author Fav24
 */
public class OperationDto {

	private MetadataDto metadata;
	private DataItemDto[] data;
	
	
	/**
	 * Constructor por defecto.
	 */
	public OperationDto() {
		metadata = null;
		data = null;
	}
	
	/**
	 * Retorna el conjunto de metadatos de la operación. 
	 * 
	 * @return el conjunto de metadatos de la operación.
	 */
	public MetadataDto getMetadata() {
		return metadata;
	}
	
	/**
	 * Asigna el conjunto de metadatos de la operación.
	 * 
	 * @param metadata El conjunto de metadatos a asoignar.
	 */
	public void setMetadata(MetadataDto metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Retorna el conjunto de elementos implicados en la operación.
	 * 
	 * @return el conjunto de elementos implicados en la operación.
	 */
	public DataItemDto[] getData() {
		return data;
	}
	
	/**
	 * Asigna el conjunto de elementos implicados en la operación.
	 * 
	 * @param data Conjunto de elementos a asignar.
	 */
	public void setData(DataItemDto[] data) {
		this.data = data;
	}
}
