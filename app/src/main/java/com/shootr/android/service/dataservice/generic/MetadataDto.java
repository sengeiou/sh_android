package com.shootr.android.service.dataservice.generic;

import java.util.HashMap;
import java.util.Map;


/**
 * Clase que contiene la estructura de una acción sobre una entidad.
 *
 * @author Fav24
 */
public class MetadataDto {

    public static class Builder {

        private static final boolean DEFAULT_INCLUDE_DELETED = false;
        private static final long DEFAULT_TOTAL_ITEMS = 1L;
        private static final long DEFAULT_OFFSET = 0L;
        private static final long DEFAULT_ITEMS = 1L;

        private final MetadataDto metadataDto;

        public Builder() {
            metadataDto = new MetadataDto();
        }

        public MetadataDto build() {
            if (metadataDto.getOperation() == null) {
                throw new IllegalStateException("Operation cannot be null");
            }
            if (metadataDto.getEntity() == null) {
                throw new IllegalStateException("Entity cannot be null");
            }
            if (metadataDto.getFilter() == null && metadataDto.getKey() == null) {
                throw new IllegalStateException("MetadataDto must use either filters or keys");
            }
            if (metadataDto.getOffset() == null) {
                metadataDto.setOffset(DEFAULT_OFFSET);
            }
            if (metadataDto.getItems() == null) {
                metadataDto.setItems(DEFAULT_ITEMS);
            }
            if (metadataDto.getTotalItems() == null) {
                metadataDto.setTotalItems(DEFAULT_TOTAL_ITEMS);
            }
            if (metadataDto.getIncludeDeleted() == null) {
                metadataDto.setIncludeDeleted(DEFAULT_INCLUDE_DELETED);
            }
            return metadataDto;
        }

        public Builder operation(String operation) {
            metadataDto.setOperation(operation);
            return this;
        }

        public Builder entity(String entity) {
            metadataDto.setEntity(entity);
            return this;
        }

        public Builder includeDeleted(boolean includeDeleted) {
            metadataDto.setIncludeDeleted(includeDeleted);
            return this;
        }

        public Builder totalItems(Long totalItems) {
            metadataDto.setTotalItems(totalItems);
            return this;
        }

        public Builder totalItems(int totalItems) {
            metadataDto.setTotalItems((long) totalItems);
            return this;
        }

        public Builder offset(Long offset) {
            metadataDto.setOffset(offset);
            return this;
        }

        public Builder offset(int offset) {
            metadataDto.setOffset((long) offset);
            return this;
        }

        public Builder items(Long items) {
            metadataDto.setItems(items);
            return this;
        }

        public Builder items(int items) {
            this.items((long) items);
            return this;
        }

        public Builder setKeys(Map<String, Object> keys) {
            metadataDto.setKey(keys);
            return this;
        }

        public Builder putKey(String key, Object value) {
            Map<String, Object> keys = metadataDto.getKey();
            if (keys == null) {
                keys = new HashMap<>();
                metadataDto.setKey(keys);
            }
            keys.put(key, value);
            return this;
        }

        public Builder filter(FilterDto filterDto) {
            metadataDto.setFilter(filterDto);
            return this;
        }
    }

    private String operation;
    private String entity;
    private Boolean includeDeleted;
    private Long totalItems;
    private Long offset;
    private Long items;
    private Map<String, Object> key;
    private FilterDto filter;

    /**
     * Constructor por defecto.
     */
    public MetadataDto() {
        this.operation = null;
        this.entity = null;
        this.includeDeleted = null;
        this.totalItems = null;
        this.offset = null;
        this.items = null;
        this.key = null;
        this.setFilter(null);
    }

    /**
     * Constructor.
     *
     * @param operation Tipo de operación a realizar.
     * @param entity Entidad contra la que se realiza la operación.
     * @param includeDeleted Flag donde se indica si se deben o no incluir registros que satisfacen las condiciones de
     * criba, con el atributo "deleted".
     * @param totalItems Número de ítems afectados por la operación.
     * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
     * @param items Número de ítems incluidos en la respuesta.
     * @param key Lista de atributos y valores que identifican el ítem a operar.
     */
    public MetadataDto(String operation, String entity, Boolean includeDeleted, Long totalItems, Long offset,
      Long items, Map<String, Object> key) {
        this.operation = operation;
        this.entity = entity;
        this.includeDeleted = includeDeleted;
        this.totalItems = totalItems;
        this.offset = offset;
        this.items = items;
        this.key = key;
    }

    /**
     * Constructor.
     *
     * @param operation Tipo de operación a realizar.
     * @param entity Entidad contra la que se realiza la operación.
     * @param includeDeleted Flag donde se indica si se deben o no incluir registros que satisfacen las condiciones de
     * criba, con el atributo "deleted".
     * @param totalItems Número de ítems afectados por la operación.
     * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
     * @param items Número de ítems incluidos en la respuesta.
     * @param filter Estructura de filtrado de los ítems a operar.
     */
    public MetadataDto(String operation, String entity, Boolean includeDeleted, Long totalItems, Long offset,
      Long items, FilterDto filter) {
        this.operation = operation;
        this.entity = entity;
        this.includeDeleted = includeDeleted;
        this.totalItems = totalItems;
        this.offset = offset;
        this.items = items;
        this.setFilter(filter);
    }

    /**
     * Retorna el tipo de operación a realizar.
     *
     * @return el tipo de operación a realizar.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Asigna el tipo de operación a realizar.
     *
     * @param operation Tipo de operación a asignar.
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Retorna el nombre de la entidad contra la que se aplicará la operación.
     *
     * @return el nombre de la entidad contra la que se aplicará la operación.
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Asigna el nombre de la entidad contra la que se aplicará la operación.
     *
     * @param entity El nombre de la entidad a asignar.
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * Retorna true o false en función de si se deben o no incluir registros que satisfacen las condiciones de criba,
     * con el atributo "deleted".
     *
     * @return true o false en función de si se deben o no incluir registros que satisfacen las condiciones de criba,
     * con el atributo "deleted".
     */
    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    /**
     * Asigna el flag donde se indica si se deben o no incluir registros que satisfacen las condiciones de criba, con el
     * atributo "deleted".
     *
     * @param includeDeleted Flag a asignar.
     */
    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    /**
     * Retorna el número de ítems afectados por la operación.
     *
     * @return el número de ítems afectados por la operación.
     */
    public Long getTotalItems() {
        return totalItems;
    }

    /**
     * Asigna el número de ítems afectados por la operación.
     *
     * @param totalItems Número de ítems a asignar.
     */
    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Retorna el número del último ítem a partir del que se desea que esta operación aplique.
     *
     * @return el número del último ítem a partir del que se desea que esta operación aplique.
     */
    public Long getOffset() {
        return offset;
    }

    /**
     * Asigna el número del último ítem a partir del que se desea que esta operación aplique.
     *
     * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
     */
    public void setOffset(Long offset) {
        this.offset = offset;
    }

    /**
     * Retorna el número de ítems incluidos en la respuesta.
     *
     * @return el número de ítems incluidos en la respuesta.
     */
    public Long getItems() {
        return items;
    }

    /**
     * Asigna el número de ítems incluidos en la respuesta.
     *
     * @param items Número de ítems incluidos en la respuesta asignar.
     */
    public void setItems(Long items) {
        this.items = items;
    }

    /**
     * Retorna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
     *
     * @return la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
     */
    public Map<String, Object> getKey() {
        return key;
    }

    /**
     * Asigna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
     *
     * @param key La lista a asignar.
     */
    public void setKey(Map<String, Object> key) {
        this.key = key;
    }

    /**
     * Retorna la estructura de filtrado de los ítems a operar.
     *
     * @return la estructura de filtrado de los ítems a operar.
     */
    public FilterDto getFilter() {
        return filter;
    }

    /**
     * Asigna la estructura de filtrado de los ítems a operar.
     *
     * @param filter La estructura de filtrado a asignar.
     */
    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }
}
