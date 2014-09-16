package gm.mobi.android.service.dataservice.generic;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * Estructura de una acción sobre una entidad.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(
creatorVisibility = JsonAutoDetect.Visibility.ANY,
fieldVisibility = JsonAutoDetect.Visibility.ANY, 
getterVisibility = JsonAutoDetect.Visibility.NONE, 
isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OperationDto {

	private MetadataDto metadata;
	private Map<String, Object>[] data;
	
	
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
	public Map<String, Object>[] getData() {
		return data;
	}
	
	/**
	 * Asigna el conjunto de elementos implicados en la operación.
	 * 
	 * @param data Conjunto de elementos a asignar.
	 */
	public void setData(Map<String, Object>[] data) {
		this.data = data;
	}
}
