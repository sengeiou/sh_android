package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Metadata;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAttribute;
import com.fav24.dataservices.domain.policy.EntityData;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.domain.policy.EntityDataAttribute.Direction;
import com.fav24.dataservices.domain.policy.EntityDataAttribute.SynchronizationField;
import com.fav24.dataservices.domain.policy.EntityKey;
import com.fav24.dataservices.domain.policy.EntityOrderAttribute;
import com.fav24.dataservices.domain.policy.EntityOrderAttribute.Order;
import com.fav24.dataservices.domain.policy.EntityOrdination;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.generic.impl.GenericServiceDataSourceInfo.EntityDataSourceInfo;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Conjunto de funciones de ayuda para la implementación JDBC del GenericService. 
 */
public class GenericServiceJDBCHelper {

	public static final char COLUMN_SCAPE_CHAR = '`';


	/**
	 * Retorna la columna envuelta entre caracteres de escape, para evitar colisiones con palabras reservadas de la sintaxis del RDBS.
	 * 
	 * @param stringBuilder Constructor de cadenas en donde se concatena la columna.
	 * @param column Nombre de la columna a concatenar.
	 * 
	 * @return el constructor de cadenas.
	 */
	public static StringBuilder scapeColumn(StringBuilder stringBuilder, String column) {

		return stringBuilder.append(COLUMN_SCAPE_CHAR).append(column).append(COLUMN_SCAPE_CHAR);
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entityAccessPolicy Política de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * 
	 * Nota: Este método se usa conjuntamente con el {@link #extractData(ResultSet, EntityAccessPolicy, AbstractList)}
	 * 
	 * @return una cadena de texto con el conjunto de campos de datos.
	 */
	public static StringBuilder getDataString(EntityAccessPolicy entityAccessPolicy, Map<String, Object> attributes) throws ServerException {

		StringBuilder resultingData = null;

		if (attributes != null) {

			Iterator<String> attributeAliases = attributes.keySet().iterator();

			while (attributeAliases.hasNext()) {

				EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAliases.next());

				if (dataAttribute == null) {
					entityAccessPolicy.checkAttributesAccesibility(new ArrayList<String>(attributes.keySet()));
				}
				else if (dataAttribute.getDirection() == Direction.OUTPUT || dataAttribute.getDirection() == Direction.BOTH) {

					if (resultingData == null) {
						resultingData = scapeColumn(new StringBuilder(), dataAttribute.getName());
					}
					else {
						resultingData.append(',');
						scapeColumn(resultingData, dataAttribute.getName());
					}
				}
			}
		}

		return resultingData;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entityAccessPolicy Políticas de acceso de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad.
	 * @param inColumns Lista en donde se retornará el conjunto de columnas de datos a insertar en el mismo orden de resolución.
	 * @param inAliases Lista en donde se retornará el conjunto de alias de datos a insertar en el mismo orden de resolución.
	 * @param outColumns Lista en donde se retornará el conjunto de columnas de datos a retornar en el mismo orden de resolución.
	 * @param outAliases Lista en donde se retornará el conjunto de alias de datos a retornar en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de datos.
	 */
	public static StringBuilder getInsertDataString(EntityAccessPolicy entityAccessPolicy, Map<String, Object> attributes, EntityDataSourceInfo entityInformation, 
			AbstractList<String> inColumns, AbstractList<String> inAliases,
			AbstractList<String> outColumns, AbstractList<String> outAliases
			) throws ServerException {

		StringBuilder resultingData = null;

		Iterator<String> attributeAliases = attributes.keySet().iterator();

		while (attributeAliases.hasNext()) {

			EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAliases.next());

			if (dataAttribute == null) {
				entityAccessPolicy.checkAttributesAccesibility(new ArrayList<String>(attributes.keySet()));
			}
			else {

				if (entityInformation.generatedData.contains(dataAttribute.getAlias())) {

					if (dataAttribute.getDirection() == Direction.BOTH || dataAttribute.getDirection() == Direction.OUTPUT) {
						outColumns.add(dataAttribute.getName());
						outAliases.add(dataAttribute.getAlias());
					}
				}
				else if (dataAttribute.getDirection() == Direction.BOTH || dataAttribute.getDirection() == Direction.INPUT) {

					inColumns.add(dataAttribute.getName());
					inAliases.add(dataAttribute.getAlias());

					if (resultingData == null) {
						resultingData = scapeColumn(new StringBuilder(), dataAttribute.getName());
					}
					else {
						resultingData.append(',');
						scapeColumn(resultingData, dataAttribute.getName());
					}
				}
			}
		}

		return resultingData;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos .
	 * 
	 * Columnas que incluye:
	 * - Conjunto completo de atributos de auditoría: #SynchronizationField.REVISION, #SynchronizationField.BIRTH, #SynchronizationField.MODIFIED, #SynchronizationField.DELETED.
	 * - Conjunto de attributos que conforman la clave primaria.
	 * - Atributos con #Direction.OUTPUT. (Para poder informar los atributos de solo salida en la operación de vuelta)
	 * 
	 * @param entityAccessPolicy Políticas de acceso de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad.
	 * 
	 * @return una cadena de texto con el conjunto de campos de datos.
	 * @throws ServerException 
	 */
	public static StringBuilder getSelectForUpdateDataString(EntityAccessPolicy entityAccessPolicy, Map<String, Object> attributes, EntityDataSourceInfo entityInformation) throws ServerException {

		StringBuilder querySelect = new StringBuilder();

		// - Conjunto completo de atributos de auditoría: #SynchronizationField.REVISION, #SynchronizationField.BIRTH, #SynchronizationField.MODIFIED, #SynchronizationField.DELETED.
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		GenericServiceJDBCHelper.scapeColumn(querySelect, revisionColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(querySelect, birthColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(querySelect, modifiedColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(querySelect, deletedColumn);

		// - Conjunto de attributos que conforman la clave primaria.
		Iterator<String> primaryKeyColumns = entityInformation.primaryKey.keySet().iterator();

		while (primaryKeyColumns.hasNext()) {

			querySelect.append(',');
			GenericServiceJDBCHelper.scapeColumn(querySelect, primaryKeyColumns.next());	
		}

		// - Atributos con #Direction.OUTPUT. (Para poder informar los atributos de solo salida en la operación de vuelta)
		Iterator<String> attributeAliases = attributes.keySet().iterator();

		while (attributeAliases.hasNext()) {

			EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAliases.next());

			if (dataAttribute == null) {
				entityAccessPolicy.checkAttributesAccesibility(new ArrayList<String>(attributes.keySet()));
			}
			else {

				if (!SynchronizationField.isSynchronizationField(dataAttribute.getAlias())) {

					if (!entityInformation.primaryKey.containsKey(dataAttribute.getName())) {

						if (dataAttribute.getDirection() == Direction.OUTPUT || dataAttribute.getDirection() == Direction.BOTH) {
							querySelect.append(',');
							GenericServiceJDBCHelper.scapeColumn(querySelect, dataAttribute.getName());
						}
					}
				}
			}
		}

		return querySelect;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 * 
	 * @param entityAccessPolicy Políticas de acceso de la entidad a la que pertenece la lista de campos filtrado.
	 * @param keys Lista de claves a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas clave en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	public static StringBuilder getKeyString(EntityAccessPolicy entityAccessPolicy, AbstractList<KeyItem> keys, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingKey = null;

		String column;
		KeyItem key = keys.get(0);

		if (key.getValue() == null) {

			column = entityAccessPolicy.getAttributeName(key.getName());

			if (column == null) {
				throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), key.getName()));
			}

			resultingKey = scapeColumn(new StringBuilder(), column).append(" IS NULL");
		}
		else {

			column = entityAccessPolicy.getAttributeName(key.getName());

			if (column == null) {
				throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), key.getName()));
			}

			resultingKey = scapeColumn(new StringBuilder(), column).append("=?");

			if (columns != null) {
				columns.add(column);
			}

			if (values != null) {
				values.add(key.getValue());
			}
		}

		for (int i=1; i<keys.size(); i++) {

			key = keys.get(i);

			resultingKey.append(" AND ");

			if (key.getValue() == null) {

				column = entityAccessPolicy.getAttributeName(key.getName());

				if (column == null) {
					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), key.getName()));
				}

				scapeColumn(resultingKey, column).append(" IS NULL");
			}
			else {

				column = entityAccessPolicy.getAttributeName(key.getName());

				if (column == null) {
					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), key.getName()));
				}

				scapeColumn(resultingKey, column).append("=?");

				if (columns != null) {
					columns.add(column);
				}

				if (values != null) {
					values.add(key.getValue());
				}
			}
		}

		return resultingKey;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entityAccessPolicy Políticas de acceso de la entidad a la que pertenece la lista de campos filtrado.
	 * @param entityInformation Información de la entidad en el subsistema.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución. No puede ser <code>null</code>.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución. No puede ser <code>null</code>.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	public static StringBuilder getFilterString(EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation, FilterItem filter, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		String column = entityAccessPolicy.getAttributeName(filter.getName());

		if (column == null) {
			throw new ServerException(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), filter.getName()));
		}

		StringBuilder resultingFilter = scapeColumn(new StringBuilder(), column);

		if (filter.getValue() == null) {

			switch(filter.getComparator()) {

			case CONTAINS:
			case STARTS:
			case ENDS:
			case EQ:
				resultingFilter.append(" IS NULL");
				break;
			case NOTCONTAINS:
			case NOTSTARTS:
			case NOTENDS:
			case NE:
				resultingFilter.append(" IS NOT NULL");
				break;
			case GT:
				resultingFilter.append(" > NULL");
				break;
			case GE:
				resultingFilter.append(" >= NULL");
				break;
			case LT:
				resultingFilter.append(" < NULL");
				break;
			case LE:			
				resultingFilter.append(" <= NULL");
				break;
			}
		}
		else {

			switch(filter.getComparator()) {

			case EQ:
				resultingFilter.append("=?");
				values.add(filter.getValue());
				break;
			case NE:
				resultingFilter.append("<>?");
				values.add(filter.getValue());
				break;
			case GT:
				resultingFilter.append(">?");
				values.add(filter.getValue());
				break;
			case GE:
				resultingFilter.append(">=?");
				values.add(filter.getValue());
				break;
			case LT:
				resultingFilter.append("<?");
				values.add(filter.getValue());
				break;
			case LE:
				resultingFilter.append("<=?");
				values.add(filter.getValue());
				break;
			case CONTAINS:
				resultingFilter.append(" LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add("%" + value.toString() + "%");
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			case STARTS:
				resultingFilter.append(" LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add(value.toString() + "%");
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			case ENDS:
				resultingFilter.append(" LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add("%" + value.toString());
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			case NOTCONTAINS:
				resultingFilter.append(" NOT LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add("%" + value.toString() + "%");
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			case NOTSTARTS:
				resultingFilter.append(" NOT LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add(value.toString() + "%");
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			case NOTENDS:
				resultingFilter.append(" NOT LIKE ?");

				try {
					CharSequence value = (CharSequence)filter.getValue();

					values.add("%" + value.toString());
				}
				catch(Throwable t) {
					values.add(filter.getValue());
				}
				break;
			}

			columns.add(column);
		}

		return resultingFilter;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entityAccessPolicy Políticas de acceso de la entidad a la que pertenece la lista de campos filtrado.
	 * @param entityInformation Información de la entidad en el subsistema.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	public static StringBuilder getFilterSetString(EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation, Filter filterSet, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		if ((filterSet.getFilterItems() == null || filterSet.getFilterItems().size() == 0) &&
				(filterSet.getFilters() == null || filterSet.getFilters().size() == 0)) {

			return null;
		}

		StringBuilder resultingFilterSet = new StringBuilder();

		resultingFilterSet.append('(');

		/*
		 * Resolución de los filtros simples.
		 */
		if (filterSet.getFilterItems() != null && filterSet.getFilterItems().size() > 0) {

			FilterItem currentFilter = filterSet.getFilterItems().get(0);

			resultingFilterSet.append(getFilterString(entityAccessPolicy, entityInformation, currentFilter, columns, values));
			for (int i=1; i<filterSet.getFilterItems().size(); i++) {

				currentFilter = filterSet.getFilterItems().get(i);

				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterString(entityAccessPolicy, entityInformation, currentFilter, columns, values));

			}
		}

		/*
		 * Resolución de los conjuntos de filtros anidados.
		 */
		if (filterSet.getFilters() != null && filterSet.getFilters().size() > 0) {
			if (filterSet.getFilterItems() != null && filterSet.getFilterItems().size() > 0) {
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
			}
			Filter currentFilterSet = filterSet.getFilters().get(0);
			resultingFilterSet.append(getFilterSetString(entityAccessPolicy, entityInformation, currentFilterSet, columns, values));
			for (int i=1; i<filterSet.getFilters().size(); i++) {

				currentFilterSet = filterSet.getFilters().get(i);
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entityAccessPolicy, entityInformation, currentFilterSet, columns, values));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet;
	}

	/**
	 * Retorna una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @param entityAccessPolicy Definición de las políticas de acceso de una determinada entidad.
	 * 
	 * @return una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @throws ServerException
	 */
	public static StringBuilder getExcludeDeletedString(EntityAccessPolicy entityAccessPolicy) throws ServerException {

		String deletedFieldName = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

		return scapeColumn(new StringBuilder(), deletedFieldName).append(" IS NULL");
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	public static StringBuilder getDefaultOrdinationString(EntityOrdination ordination) throws ServerException {

		StringBuilder resultingOrdination = null;

		for(EntityOrderAttribute orderAttribute : ordination.getOrder()) {

			if (resultingOrdination == null) {
				resultingOrdination = scapeColumn(new StringBuilder(), orderAttribute.getName());
			}
			else {
				resultingOrdination.append(", ");
				scapeColumn(resultingOrdination, orderAttribute.getName());
			}

			if (orderAttribute.getOrder() == Order.ASCENDING) {

				resultingOrdination.append(" ASC");
			}
			else if (orderAttribute.getOrder() == Order.DESCENDING) {

				resultingOrdination.append(" DESC");
			}
		}

		return resultingOrdination;
	}

	/**
	 * Método para la conversión de tipos de datos.
	 * 
	 * Este método se usa de DTO/Domain a JDBC.
	 * 
	 * @param destinationType Tipo de dato al que se desea convertir el valor.
	 * @param value Valor a convertir.
	 * 
	 * @return valor convertido.
	 */
	public static Object translateToType(int destinationType, Object value) {

		if (value != null) {

			switch(destinationType) {
			case java.sql.Types.DATE:
				if (value instanceof Number) {
					return new Date(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Date.valueOf((String)value);
				}
				break;
			case java.sql.Types.TIME:
				if (value instanceof Number) {
					return new Time(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Time.valueOf((String)value);
				}
				break;
			case java.sql.Types.TIMESTAMP:
				if (value instanceof Number) {
					return new Timestamp(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Timestamp.valueOf((String)value);
				}
				break;
			}
		}

		return value;
	}

	/**
	 * Método para la conversión de tipos de datos.
	 * 
	 * Este método se usa de JDBC a DTO/Domain.
	 * 
	 * @param originType Tipo de dato del que se desea convertir el valor.
	 * @param value Valor a convertir.
	 * 
	 * @return valor convertido.
	 */
	public static Object translateFromType(int originType, Object value) {

		if (value != null) {

			switch(originType) {

			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
				if (value instanceof java.util.Date) {
					return ((java.util.Date)value).getTime();
				}
				else if (value instanceof Number) {
					return ((Number)value).longValue();
				}
				break;
			}
		}

		return value;
	}

	/**
	 * Extrae el conjunto de datos del set de resultados y lo añade a la estructura de datos de la operación.   
	 * 
	 * @param resultSet Set de resultados del que se extrae la información.
	 * @param entityAccessPolicy Políticas de acceso de la entidad.
	 * @param entityInformation Información de la entidad en el subsistema.
	 * @param data Salida de la información extraida. 
	 * 
	 * Nota: Este método se usa conjuntamente con el {@link #getDataString(EntityAccessPolicy, Map)}
	 * 
	 * @return el número de elementos extraidos.  
	 * 
	 * @throws SQLException
	 */
	public static long extractData(ResultSet resultSet, EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation, AbstractList<DataItem> data) throws SQLException {

		long numItems = 0;

		if (data != null && data.size() > 0) {

			DataItem referenceDataItem = new DataItem(data.get(0));

			if (referenceDataItem != null && referenceDataItem.getAttributes() != null && referenceDataItem.getAttributes().size() > 0) {

				int itemIndex = 0;

				if (resultSet.first()) {

					EntityData entityData = entityAccessPolicy.getData();

					do
					{
						DataItem dataItem;

						if (data.size() <= itemIndex) {
							data.add(dataItem = new DataItem(referenceDataItem));
						}
						else {
							dataItem = data.get(itemIndex);
						}
						itemIndex++;

						int i=1;
						for (String attributeAlias : dataItem.getAttributes().keySet()) {

							EntityDataAttribute dataAttribute = entityData.getAttribute(attributeAlias);

							if (dataAttribute != null && (dataAttribute.getDirection() == Direction.BOTH || dataAttribute.getDirection() == Direction.OUTPUT)) {

								Object value = resultSet.getObject(i++);

								if (resultSet.wasNull()) {
									value = null;
								}
								else {

									value = translateFromType(entityInformation.dataFields.get(dataAttribute.getName()), value);
								}

								dataItem.getAttributes().put(attributeAlias, value);
							}
						}

						numItems++;
					}while(resultSet.next());
				}
			}
		}

		return numItems;
	}

	/**
	 * Recupera del subsistema la row marcada como eliminada, de la operación indicada. 
	 * 
	 * @param connection Conexión JDBC a usar para realizar la recuperación.
	 * @param dataItem Elemento que contiene información de la fila a recuperar y actualizar.
	 * @param entityAccessPolicy Políticas de acceso de la entidad.
	 * @param entityInformation Información de la entidad en el subsistema. 
	 * 
	 * @return true o false en función de si el registro ha podido ser reciclado o no.
	 * 
	 * @throws ServerException 
	 */
	public static boolean recoverRow(Connection connection, DataItem dataItem, EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation) throws ServerException {

		StringBuilder recoverQuery = new StringBuilder(); 
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		int revisionColumnType = entityInformation.dataFields.get(revisionColumn);
		int birthColumnType = entityInformation.dataFields.get(birthColumn);
		int modifiedColumnType = entityInformation.dataFields.get(modifiedColumn);
		int deletedColumnType = entityInformation.dataFields.get(deletedColumn);

		recoverQuery.append("UPDATE ").append(entityAccessPolicy.getName().getName());

		AbstractList<Integer> types = new ArrayList<Integer>();
		AbstractList<Object> params = new ArrayList<Object>();

		recoverQuery.append(" SET ");
		scapeColumn(recoverQuery, revisionColumn).append("=?,");
		scapeColumn(recoverQuery, birthColumn).append("=?,");
		scapeColumn(recoverQuery, modifiedColumn).append("=?,");
		scapeColumn(recoverQuery, deletedColumn).append("=?");

		types.add(revisionColumnType);
		types.add(birthColumnType);
		types.add(modifiedColumnType);
		types.add(deletedColumnType);

		params.add(translateToType(revisionColumnType, dataItem.getAttributes().get(SynchronizationField.REVISION.getSynchronizationField())));
		params.add(translateToType(birthColumnType, dataItem.getAttributes().get(SynchronizationField.BIRTH.getSynchronizationField())));
		params.add(translateToType(modifiedColumnType, dataItem.getAttributes().get(SynchronizationField.MODIFIED.getSynchronizationField())));
		params.add(translateToType(deletedColumnType, dataItem.getAttributes().get(SynchronizationField.DELETED.getSynchronizationField())));

		for (EntityDataAttribute attribute : entityAccessPolicy.getData().getData()) {

			if (!SynchronizationField.isSynchronizationField(attribute.getAlias())) {

				if (!entityInformation.generatedData.contains(attribute.getName())) {

					recoverQuery.append(',');
					scapeColumn(recoverQuery, attribute.getName()).append("=?");

					types.add(entityInformation.dataFields.get(attribute.getName()));

					if (dataItem.getAttributes().containsKey(attribute.getAlias())) {
						params.add(dataItem.getAttributes().get(attribute.getAlias()));
					}
					else {
						params.add(entityInformation.dataFieldsDefaults.get(attribute.getName()));
					}
				}
			}
		}

		recoverQuery.append(" WHERE ");
		scapeColumn(recoverQuery, deletedColumn).append(" IS NOT NULL");

		/*
		 * Montaje del filtro:
		 * - Se realliza un FND (Forma Normal Disyuntiva) con los campos de las claves primarias.
		 * - Si el campo es NULL, no se incluye en el filtro.
		 * 
		 * Interpretación del resultado del filtrado:
		 * - Si se obtiene más de una fila, significa que la recuperación implica una clave duplicada, y saltará una excepción.
		 * - Si no se obtiene ninguna fila, significa que el intento de inserción prévio a la llamada a este método, falló o bien por clave duplicada, o bien por campo obligatorio.
		 * - Si se obtiene exactamente una fila, la recuperación ha sido un éxito. 
		 */

		StringBuilder fndQuery = null;
		AbstractList<Integer> fndTypes = new ArrayList<Integer>();
		AbstractList<Object> fndParams = new ArrayList<Object>();

		for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

			StringBuilder keyQuery = null;

			Integer[] keyTypes = null;
			Object[] keyParams = null;
			int i = 0;

			for (EntityAttribute key : entityKey.getKey()) {

				Object value = dataItem.getAttributes().get(key.getAlias());

				if (value != null) {

					String columnName = entityAccessPolicy.getData().getAttribute(key.getAlias()).getName();

					if (keyQuery == null) {

						keyQuery = new StringBuilder("(");

						keyTypes = new Integer[entityKey.getKey().size()];
						keyParams = new Object[keyTypes.length];
					}
					else {

						keyQuery.append(" AND ");
					}

					scapeColumn(keyQuery, columnName).append("=?");

					keyTypes[i] = entityInformation.dataFields.get(columnName);
					keyParams[i] = value;
					i++;
				}
				else {
					keyTypes = null;
					keyParams = null;
					keyQuery = null;
					break;
				}
			}

			if (keyQuery != null) {

				keyQuery.append(')');

				if (fndQuery == null) {
					fndQuery = keyQuery;
				}
				else {
					fndQuery.append(" OR ").append(keyQuery);
				}

				for (i=0; i < keyTypes.length; i++) {

					fndTypes.add(keyTypes[i]);
					fndParams.add(keyParams[i]);
				}
			}
		}

		if (fndQuery == null) {
			throw new ServerException(GenericService.ERROR_CREATE_DUPLICATE_ROW, String.format(GenericService.ERROR_CREATE_DUPLICATE_ROW_MESSAGE, "DML: " + recoverQuery + ". Valores: " + params.toString()));
		}

		recoverQuery.append(" AND (").append(fndQuery).append(')');

		/*
		 * Recuperación del registro.
		 */
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(recoverQuery.toString());

			int j=1;
			for (int i=0; i<types.size(); i++, j++) {

				Object value = params.get(i);

				if (value == null) {

					preparedStatement.setNull(j, types.get(i));
				}
				else {

					preparedStatement.setObject(j, value, types.get(i));
				}
			}

			for (int i=0; i<fndTypes.size(); i++, j++) {

				Object value = fndParams.get(i);

				if (value == null) {

					preparedStatement.setNull(j, fndTypes.get(i));
				}
				else {

					preparedStatement.setObject(j, value, fndTypes.get(i));
				}
			}

			int modifiedRows = preparedStatement.executeUpdate();

			if (modifiedRows != 1) {
				return false;
			}
		}
		catch(SQLException e) {
			throw new ServerException(GenericService.ERROR_REFURBISHING_ROW, String.format(GenericService.ERROR_REFURBISHING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		/*
		 * Recuperación de los datos del registro. 
		 */
		StringBuilder recoveredQuery = new StringBuilder("SELECT ");
		AbstractList<String> attributes = new ArrayList<String>();
		AbstractList<Integer> attributesType = new ArrayList<Integer>();

		boolean firstField = true;
		for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

			for (EntityAttribute key : entityKey.getKey()) {

				Object value = dataItem.getAttributes().get(key.getAlias());

				if (value == null) {

					if (firstField) {
						firstField = false;
					}
					else {
						recoveredQuery.append(',');
					}

					scapeColumn(recoveredQuery, entityAccessPolicy.getData().getAttribute(key.getAlias()).getName());

					attributes.add(key.getAlias());
					attributesType.add(entityInformation.keyFields.get(key.getName()));
				}
			}
		}

		// En caso de necesitar recuperar alguna atributo de la key (sobretodo en caso de keys compuestas), se consulta el registro.
		if (!firstField) {
			
			recoveredQuery.append(" FROM ").append(entityAccessPolicy.getName().getName());

			recoveredQuery.append(" WHERE ").append(fndQuery);

			ResultSet resultSet = null;

			try {
				preparedStatement = connection.prepareStatement(recoveredQuery.toString());

				for (int i=0; i<fndTypes.size(); i++) {
					preparedStatement.setObject(i+1, fndParams.get(i), fndTypes.get(i));
				}

				resultSet = preparedStatement.executeQuery();

				if (resultSet.first()) {

					int i=1;
					for (String attributeAlias : attributes) {

						Object value = resultSet.getObject(i++);

						if (resultSet.wasNull()) {
							value = null;
						}
						else {

							value = translateFromType(attributesType.get(i-1), value);
						}

						dataItem.getAttributes().put(attributeAlias, value);
					}
				}
				else {
					throw new ServerException(GenericService.ERROR_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
				}

				if (resultSet.next()) {
					throw new ServerException(GenericService.ERROR_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
				}
			}
			catch(SQLException e) {
				throw new ServerException(GenericService.ERROR_REFURBISHING_ROW, String.format(GenericService.ERROR_REFURBISHING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
			}
			finally {
				JDBCUtils.CloseQuietly(resultSet);
				JDBCUtils.CloseQuietly(preparedStatement);
			}
		}

		return true;
	}


	/**
	 * Modifica los datos del registro en curso, en caso de ser necesario, y añade el cambio a la estructura de la operación.
	 * Hace uso del algoritmo general que decide si "gana" el registro local en curso, o el entrante representado por el item indicado.
	 * 
	 * @param connection Conexión a la base de datos, con la transacción abierta para que el posible update, entre dentro.
	 * @param resultSet Registro de base de datos sobre el que se evalua su posible actualización.
	 * @param now Momento en el que se inicia la operación de modificación.
	 * @param dataItem Elemento que contiene información de la fila a actualizar.
	 * @param entityAccessPolicy Políticas de acceso de la entidad sobre la que se realiza el update.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad sobre la que se realiza el update.
	 * @param metadata El conjunto de metadatos de la operación.
	 * 
	 * @return true o false en caso de que la operación haya o no tenido éxito. El fallo puede ser debido a que no ha sido posible localizar el registro a modificar, o que se a localizado más de 1.
	 * 
	 * @throws ServerException 
	 */
	public static boolean updateRow(Connection connection, ResultSet resultSet, Timestamp now, DataItem dataItem, EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation, Metadata metadata) throws ServerException {

		NavigableMap<String, Object> attributes = dataItem.getAttributes();

		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED

		int revisionColumnType = entityInformation.dataFields.get(revisionColumn);
		int modifiedColumnType = entityInformation.dataFields.get(modifiedColumn);

		Timestamp localBirth = null;
		Timestamp localModified = null;
		Long localRevision = null;

		PreparedStatement preparedStatement = null;

		try {

			localBirth = resultSet.getTimestamp(birthColumn);
			localBirth.setNanos(0);

			localModified = resultSet.getTimestamp(modifiedColumn);
			localModified.setNanos(0);

			localRevision = resultSet.getLong(revisionColumn);

			Timestamp remoteModified = new Timestamp((Long)attributes.get(SynchronizationField.MODIFIED.getSynchronizationField()));
			remoteModified.setNanos(0);

			Long remoteRevision = ((Number)attributes.get(SynchronizationField.REVISION.getSynchronizationField())).longValue();

			// Comprobación de si gana el registro local, o el remoto.
			if (entityAccessPolicy.getIncommingAlwaysWins() || metadata.getIncommingItemsAlwaysWins() || 
					GenericServiceBasic.incommingItemWins(localModified, remoteModified, localRevision, remoteRevision, 
							entityAccessPolicy.getPositiveRevisionThreshold(), entityAccessPolicy.getNegativeRevisionThreshold())) {

				// Gana el registro entrante.
				Long finalRevision = localRevision == remoteRevision ? remoteRevision + 1 : remoteRevision;

				// Propagación de la información de auditoría.
				attributes.put(SynchronizationField.REVISION.getSynchronizationField(), finalRevision);
				attributes.put(SynchronizationField.BIRTH.getSynchronizationField(), localBirth.getTime());
				attributes.put(SynchronizationField.MODIFIED.getSynchronizationField(), now.getTime());
				attributes.put(SynchronizationField.DELETED.getSynchronizationField(), null);

				// Propagación de la información de solo salida.
				AbstractList<Integer> types = new ArrayList<Integer>();
				AbstractList<Object> params = new ArrayList<Object>();

				StringBuilder queryUpdate = new StringBuilder("UPDATE ").append(entityAccessPolicy.getName().getName()).append(" SET ");
				GenericServiceJDBCHelper.scapeColumn(queryUpdate, revisionColumn).append("=?,");
				GenericServiceJDBCHelper.scapeColumn(queryUpdate, modifiedColumn).append("=?");

				types.add(revisionColumnType);
				params.add(finalRevision);

				types.add(modifiedColumnType);
				params.add(now);

				for (String attributeAlias : attributes.keySet()) {

					if (!SynchronizationField.isSynchronizationField(attributeAlias)) {

						EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);

						if (dataAttribute != null) {

							if (dataAttribute.getDirection() == Direction.OUTPUT) {

								Object value = resultSet.getObject(dataAttribute.getName());

								if (resultSet.wasNull()) {
									value = null;
								}
								else {
									value = translateFromType(entityInformation.dataFields.get(dataAttribute.getName()), value);
								}

								attributes.put(attributeAlias, value);
							}
							else if (!entityInformation.primaryKey.containsKey(dataAttribute.getName())) {

								types.add(entityInformation.dataFields.get(dataAttribute.getName()));
								params.add(attributes.get(attributeAlias));

								queryUpdate.append(',');
								GenericServiceJDBCHelper.scapeColumn(queryUpdate, dataAttribute.getName()).append("=?");
							}
						}
					}
				}

				// Concatenación del filtro por clave primaira.
				queryUpdate.append(" WHERE ");

				// - Conjunto de attributos que conforman la clave primaria.
				Iterator<Entry<String, Integer>> primaryKeyColumns = entityInformation.primaryKey.entrySet().iterator();

				Entry<String, Integer> primaryKeyColumn = primaryKeyColumns.next();
				GenericServiceJDBCHelper.scapeColumn(queryUpdate, primaryKeyColumn.getKey()).append("=?");

				types.add(primaryKeyColumn.getValue());
				params.add(resultSet.getObject(primaryKeyColumn.getKey()));

				while (primaryKeyColumns.hasNext()) {

					primaryKeyColumn = primaryKeyColumns.next();

					queryUpdate.append(" AND ");
					GenericServiceJDBCHelper.scapeColumn(queryUpdate, primaryKeyColumn.getKey()).append("=?");

					types.add(primaryKeyColumn.getValue());
					params.add(resultSet.getObject(primaryKeyColumn.getKey()));
				}


				preparedStatement = connection.prepareStatement(queryUpdate.toString());

				for (int i=0; i<params.size(); i++) {

					Object value = params.get(i);

					if (value == null) {

						preparedStatement.setNull(i+1, types.get(i));
					}
					else {

						preparedStatement.setObject(i+1, value, types.get(i));
					}
				}

				int updatedRegisters = preparedStatement.executeUpdate();

				if (updatedRegisters == 0) {
					return false;
				}

				if (updatedRegisters > 1) {
					return false;
				}

			}
			else { // Gana el registro existente.

				// Propagación de la información de auditoría.
				attributes.put(SynchronizationField.REVISION.getSynchronizationField(), localRevision);
				attributes.put(SynchronizationField.BIRTH.getSynchronizationField(), localBirth.getTime());
				attributes.put(SynchronizationField.MODIFIED.getSynchronizationField(), localModified.getTime());
				attributes.put(SynchronizationField.DELETED.getSynchronizationField(), null);

				// Propagación de la información de salida.
				for (String attributeAlias : attributes.keySet()) {

					if (!SynchronizationField.isSynchronizationField(attributeAlias)) {

						EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);

						if (dataAttribute != null && (dataAttribute.getDirection() == Direction.OUTPUT || dataAttribute.getDirection() == Direction.BOTH)) {

							Object value = resultSet.getObject(dataAttribute.getName());

							if (resultSet.wasNull()) {
								value = null;
							}
							else {

								value = translateFromType(entityInformation.dataFields.get(dataAttribute.getName()), value);
							}

							attributes.put(attributeAlias, value);
						}
					}
				}
			}
		}
		catch (SQLException e) {
			throw new ServerException(GenericService.ERROR_UPDATING_ROW, String.format(GenericService.ERROR_UPDATING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		return true;
	}

	/**
	 * Recupera del subsistema la row marcada como eliminada o modifica la existente, de la operación indicada. 
	 * 
	 * @param connection Conexión JDBC a usar para realizar la recuperación o modificación.
	 * @param now Momento en el que se inicia la operación de modificación.
	 * @param dataItem Elemento que contiene información de la fila a recuperar o modificar y actualizar.
	 * @param entityAccessPolicy Políticas de acceso de la entidad.
	 * @param entityInformation Información de la entidad en el subsistema.
	 * @param metadata El conjunto de metadatos de la operación.
	 * 
	 * @return true o false en función de si el registro ha podido ser reciclado, o modificado.
	 * 
	 * @throws ServerException 
	 */
	public static boolean recoverOrUpdateRow(Connection connection, Timestamp now, DataItem dataItem, EntityAccessPolicy entityAccessPolicy, EntityDataSourceInfo entityInformation, Metadata metadata) throws ServerException {

		StringBuilder recoverQuery = new StringBuilder(); 
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		int revisionColumnType = entityInformation.dataFields.get(revisionColumn);
		int birthColumnType = entityInformation.dataFields.get(birthColumn);
		int modifiedColumnType = entityInformation.dataFields.get(modifiedColumn);
		int deletedColumnType = entityInformation.dataFields.get(deletedColumn);

		recoverQuery.append("UPDATE ").append(entityAccessPolicy.getName().getName());

		AbstractList<Integer> types = new ArrayList<Integer>();
		AbstractList<Object> params = new ArrayList<Object>();

		recoverQuery.append(" SET ");
		scapeColumn(recoverQuery, revisionColumn).append("=?,");
		scapeColumn(recoverQuery, birthColumn).append("=?,");
		scapeColumn(recoverQuery, modifiedColumn).append("=?,");
		scapeColumn(recoverQuery, deletedColumn).append("=?");

		types.add(revisionColumnType);
		types.add(birthColumnType);
		types.add(modifiedColumnType);
		types.add(deletedColumnType);

		params.add(translateToType(revisionColumnType, dataItem.getAttributes().get(SynchronizationField.REVISION.getSynchronizationField())));
		params.add(translateToType(birthColumnType, dataItem.getAttributes().get(SynchronizationField.BIRTH.getSynchronizationField())));
		params.add(translateToType(modifiedColumnType, dataItem.getAttributes().get(SynchronizationField.MODIFIED.getSynchronizationField())));
		params.add(translateToType(deletedColumnType, dataItem.getAttributes().get(SynchronizationField.DELETED.getSynchronizationField())));

		for (EntityDataAttribute attribute : entityAccessPolicy.getData().getData()) {

			if (!SynchronizationField.isSynchronizationField(attribute.getAlias())) {

				if (!entityInformation.generatedData.contains(attribute.getName())) {

					recoverQuery.append(',');
					scapeColumn(recoverQuery, attribute.getName()).append("=?");

					types.add(entityInformation.dataFields.get(attribute.getName()));

					if (dataItem.getAttributes().containsKey(attribute.getAlias())) {
						params.add(dataItem.getAttributes().get(attribute.getAlias()));
					}
					else {
						params.add(entityInformation.dataFieldsDefaults.get(attribute.getName()));
					}
				}
			}
		}

		recoverQuery.append(" WHERE ");
		scapeColumn(recoverQuery, deletedColumn).append(" IS NOT NULL");

		/*
		 * Montaje del filtro de claves:
		 * - Se realliza un FND (Forma Normal Disyuntiva) con los campos de las claves primarias.
		 * - Si el campo es NULL, no se incluye en el filtro.
		 * 
		 * Interpretación del resultado del filtrado:
		 * - Si se obtiene más de una fila, significa que la recuperación implica una clave duplicada, y saltará una excepción.
		 * - Si no se obtiene ninguna fila, significa que el intento de inserción prévio a la llamada a este método, falló o bien por clave duplicada, o bien por campo obligatorio.
		 * - Si se obtiene exactamente una fila, la recuperación ha sido un éxito. 
		 */

		StringBuilder fndQuery = null;
		AbstractList<Integer> fndTypes = new ArrayList<Integer>();
		AbstractList<Object> fndParams = new ArrayList<Object>();

		for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

			StringBuilder keyQuery = null;

			Integer[] keyTypes = null;
			Object[] keyParams = null;
			int i = 0;

			for (EntityAttribute key : entityKey.getKey()) {

				Object value = dataItem.getAttributes().get(key.getAlias());

				if (value != null) {

					String columnName = entityAccessPolicy.getData().getAttribute(key.getAlias()).getName();

					if (keyQuery == null) {

						keyQuery = new StringBuilder("(");

						keyTypes = new Integer[entityKey.getKey().size()];
						keyParams = new Object[keyTypes.length];
					}
					else {

						keyQuery.append(" AND ");
					}

					scapeColumn(keyQuery, columnName).append("=?");

					keyTypes[i] = entityInformation.dataFields.get(columnName);
					keyParams[i] = value;
					i++;
				}
				else {
					keyTypes = null;
					keyParams = null;
					keyQuery = null;
					break;
				}
			}

			if (keyQuery != null) {

				keyQuery.append(')');

				if (fndQuery == null) {
					fndQuery = keyQuery;
				}
				else {
					fndQuery.append(" OR ").append(keyQuery);
				}

				for (i=0; i < keyTypes.length; i++) {

					fndTypes.add(keyTypes[i]);
					fndParams.add(keyParams[i]);
				}
			}
		}

		if (fndQuery == null) {
			throw new ServerException(GenericService.ERROR_DUPLICATED_ROW, String.format(GenericService.ERROR_DUPLICATED_ROW_MESSAGE, "DML: " + recoverQuery + ". Valores: " + params.toString()));
		}

		recoverQuery.append(" AND (").append(fndQuery).append(')');

		/*
		 * Recuperación del registro.
		 */
		PreparedStatement recoverQueryStatement = null;
		int modifiedRows = 0;

		try {
			recoverQueryStatement = connection.prepareStatement(recoverQuery.toString());

			int j=1;
			for (int i=0; i<types.size(); i++, j++) {

				Object value = params.get(i);

				if (value == null) {

					recoverQueryStatement.setNull(j, types.get(i));
				}
				else {

					recoverQueryStatement.setObject(j, value, types.get(i));
				}
			}

			for (int i=0; i<fndTypes.size(); i++, j++) {

				Object value = fndParams.get(i);

				if (value == null) {

					recoverQueryStatement.setNull(j, fndTypes.get(i));
				}
				else {

					recoverQueryStatement.setObject(j, value, fndTypes.get(i));
				}
			}

			modifiedRows = recoverQueryStatement.executeUpdate();

			if (modifiedRows > 1) {
				throw new ServerException(GenericService.ERROR_DUPLICATED_ROW, String.format(GenericService.ERROR_DUPLICATED_ROW_MESSAGE, "DML: " + recoverQuery + ". Valores: " + params.toString()));
			}
		}
		catch(SQLException e) {
			throw new ServerException(GenericService.ERROR_REFURBISHING_ROW, String.format(GenericService.ERROR_REFURBISHING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(recoverQueryStatement);
		}

		if (modifiedRows == 0) {
			/*
			 * Recuperación de los datos del registro existente, y preparación para su posible modificación. 
			 */
			StringBuilder querySelect = new StringBuilder("SELECT ");
			querySelect.append(GenericServiceJDBCHelper.getSelectForUpdateDataString(entityAccessPolicy, dataItem.getAttributes(), entityInformation));
			querySelect.append(" FROM ").append(entityAccessPolicy.getName().getName());
			querySelect.append(" WHERE ");
			GenericServiceJDBCHelper.scapeColumn(querySelect, deletedColumn).append(" IS NULL");
			querySelect.append(" AND (").append(fndQuery).append(')');

			PreparedStatement selectQueryStatement = null;
			ResultSet resultSet = null;
			try {
				selectQueryStatement = connection.prepareStatement(querySelect.toString());

				int j=1;
				for (int i=0; i<fndTypes.size(); i++, j++) {

					Object value = fndParams.get(i);

					if (value == null) {

						selectQueryStatement.setNull(j, fndTypes.get(i));
					}
					else {

						selectQueryStatement.setObject(j, value, fndTypes.get(i));
					}
				}

				resultSet = selectQueryStatement.executeQuery();

				if (resultSet.first()) {

					// Sólo puede haber una row.
					if (!resultSet.isLast()) {
						throw new ServerException(GenericService.ERROR_DUPLICATED_ROW, String.format(GenericService.ERROR_DUPLICATED_ROW_MESSAGE, "DML: " + recoverQuery + ". Valores: " + params.toString()));
					}

					return updateRow(connection, resultSet, now, dataItem, entityAccessPolicy, entityInformation, metadata);
				}
				else {
					return false;
				}
			}
			catch(SQLException e) {
				throw new ServerException(GenericService.ERROR_UPDATING_ROW, String.format(GenericService.ERROR_UPDATING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
			}
			finally {
				JDBCUtils.CloseQuietly(resultSet);
				JDBCUtils.CloseQuietly(selectQueryStatement);
			}
		}
		else { //if (modifiedRows == 1)
			/*
			 * Recuperación de los datos del registro recuperado. 
			 */
			StringBuilder recoveredQuery = new StringBuilder("SELECT ");
			AbstractList<String> attributes = new ArrayList<String>();
			AbstractList<Integer> attributesType = new ArrayList<Integer>();

			boolean firstField = true;
			for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

				for (EntityAttribute key : entityKey.getKey()) {

					Object value = dataItem.getAttributes().get(key.getAlias());

					if (value == null) {

						if (firstField) {
							firstField = false;
						}
						else {
							recoveredQuery.append(',');
						}

						scapeColumn(recoveredQuery, entityAccessPolicy.getData().getAttribute(key.getAlias()).getName());

						attributes.add(key.getAlias());
						attributesType.add(entityInformation.keyFields.get(key.getName()));
					}
				}
			}

			/*
			 *  Si no hay campos clave en la sección data a recuperar, no se ejecuta la sentencia de recuperación. 
			 */
			if (!firstField) {

				recoveredQuery.append(" FROM ").append(entityAccessPolicy.getName().getName());

				recoveredQuery.append(" WHERE ").append(fndQuery);

				PreparedStatement recoveredQueryStatement = null;
				ResultSet resultSet = null;

				try {
					recoveredQueryStatement = connection.prepareStatement(recoveredQuery.toString());

					for (int i=0; i<fndTypes.size(); i++) {

						Object value = fndParams.get(i);

						if (value == null) {

							recoveredQueryStatement.setNull(i+1, fndTypes.get(i));
						}
						else {

							recoveredQueryStatement.setObject(i+1, value, fndTypes.get(i));
						}
					}

					resultSet = recoveredQueryStatement.executeQuery();

					if (resultSet.first()) {

						int i=1;
						for (String attributeAlias : attributes) {

							Object value = resultSet.getObject(i++);

							if (resultSet.wasNull()) {
								value = null;
							}
							else {

								value = translateFromType(attributesType.get(i-1), value);
							}

							dataItem.getAttributes().put(attributeAlias, value);
						}
					}
					else {
						throw new ServerException(GenericService.ERROR_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
					}

					if (resultSet.next()) {
						throw new ServerException(GenericService.ERROR_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
					}
				}
				catch(SQLException e) {
					throw new ServerException(GenericService.ERROR_REFURBISHING_ROW, String.format(GenericService.ERROR_REFURBISHING_ROW_MESSAGE, dataItem.toString(), e.getMessage()));
				}
				finally {
					JDBCUtils.CloseQuietly(resultSet);
					JDBCUtils.CloseQuietly(recoveredQueryStatement);
				}
			}

			return true;
		}
	}
}
