package gm.mobi.android.service.dataservice.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.drive.Metadata;


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

    public static class Builder {

        private final OperationDto operationDto;

        public Builder() {
            operationDto = new OperationDto();
        }

        public OperationDto build() {
            return operationDto;
        }

        public Builder metadata(MetadataDto metadataDto) {
            operationDto.setMetadata(metadataDto);
            return this;
        }

        public Builder setData(Map<String, Object>[] data) {
            operationDto.setData(data);
            return this;
        }

        public Builder putData(Map<String, Object> dataItem) {
            Map<String, Object>[] datas = operationDto.getData();
            if (datas != null) {
                List<Map<String, Object>> datasList = Arrays.asList(datas);
                datasList.add(dataItem);
                operationDto.setData(datasList.toArray(new HashMap[datasList.size()]));
            } else {
                datas = new HashMap[1];
                datas[0] = dataItem;
                operationDto.setData(datas);
            }
            return this;
        }


    }

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
