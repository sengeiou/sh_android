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

import org.springframework.dao.DataAccessException;

import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Metadata;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute.SynchronizationField;
import com.fav24.dataservices.domain.security.EntityKey;
import com.fav24.dataservices.domain.security.EntityOrderAttribute;
import com.fav24.dataservices.domain.security.EntityOrderAttribute.Order;
import com.fav24.dataservices.domain.security.EntityOrdination;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.generic.impl.GenericServiceJDBC.EntityJDBCInformation;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Conjunto de funciones de ayuda para la implementación JDBC del GenericService. 
 */
public class GenericServiceJDBCHelper {

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	public static StringBuilder getDataString(String entity, Map<String, Object> attributes) throws ServerException {

		StringBuilder resultingData = new StringBuilder();

		if (attributes != null) {

			Iterator<String> attributeAliases = attributes.keySet().iterator();

			if (attributeAliases.hasNext()) {

				String column = AccessPolicy.getAttributeName(entity, attributeAliases.next());

				resultingData.append(column);

				while (attributeAliases.hasNext()) {

					String attributeAlias = attributeAliases.next();
					column = AccessPolicy.getAttributeName(entity, attributeAlias);

					if (column == null) {
						AccessPolicy.checkAttributesAccesibility(entity, new ArrayList<String>(attributes.keySet()));
					}
					else {
						resultingData.append(',');
						resultingData.append(column);
					}
				}
			}
		}
		else {
			resultingData.append("count(*)");
		}

		return resultingData;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad.
	 * @param inColumns Lista en donde se retornará el conjunto de columnas de datos a insertar en el mismo orden de resolución.
	 * @param inAliases Lista en donde se retornará el conjunto de alias de datos a insertar en el mismo orden de resolución.
	 * @param outColumns Lista en donde se retornará el conjunto de columnas de datos a retornar en el mismo orden de resolución.
	 * @param outAliases Lista en donde se retornará el conjunto de alias de datos a retornar en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	public static StringBuilder getInsertDataString(String entity, Map<String, Object> attributes, EntityJDBCInformation entityInformation, 
			AbstractList<String> inColumns, AbstractList<String> inAliases,
			AbstractList<String> outColumns, AbstractList<String> outAliases
			) throws ServerException {

		StringBuilder resultingData = new StringBuilder();

		Iterator<String> attributeAliases = attributes.keySet().iterator();

		if (attributeAliases.hasNext()) {

			String alias = attributeAliases.next();
			String column = AccessPolicy.getAttributeName(entity, alias);

			if (entityInformation.generatedData.contains(column)) {
				outColumns.add(column);
				outAliases.add(alias);
			}
			else {
				inColumns.add(column);
				inAliases.add(alias);
				resultingData.append(column);
			}

			while (attributeAliases.hasNext()) {

				alias = attributeAliases.next();
				column = AccessPolicy.getAttributeName(entity, alias);

				if (column == null) {
					AccessPolicy.checkAttributesAccesibility(entity, new ArrayList<String>(attributes.keySet()));
				}
				else {
					if (entityInformation.generatedData.contains(column)) {
						outColumns.add(column);
						outAliases.add(alias);
					}
					else {
						inColumns.add(column);
						inAliases.add(alias);
						resultingData.append(',');
						resultingData.append(column);
					}
				}
			}
		}

		return resultingData;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de claves.
	 * @param keys Lista de claves a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas clave en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	public static StringBuilder getKeyString(String entity, AbstractList<KeyItem> keys, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingKey = new StringBuilder();

		String column;
		KeyItem key = keys.get(0);

		if (key.getValue() == null) {

			resultingKey.append(AccessPolicy.getAttributeName(entity, key.getName())).append(" IS NULL");
		}
		else {

			column = AccessPolicy.getAttributeName(entity, key.getName());

			resultingKey.append(column).append('=').append('?');

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

				resultingKey.append(AccessPolicy.getAttributeName(entity, key.getName())).append(" IS NULL");
			}
			else {

				column = AccessPolicy.getAttributeName(entity, key.getName());

				resultingKey.append(column).append('=').append('?');

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
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	public static StringBuilder getFilterString(String entity, FilterItem filter, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingFilter = new StringBuilder();

		String column = AccessPolicy.getAttributeName(entity, filter.getName());
		resultingFilter.append(column);

		if (filter.getValue() == null) {

			switch(filter.getComparator()) {

			case EQ:
				resultingFilter.append(" IS NULL");
				break;
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
				resultingFilter.append(" = ");
				break;
			case NE:
				resultingFilter.append(" <> ");
				break;
			case GT:
				resultingFilter.append(" > ");
				break;
			case GE:
				resultingFilter.append(" >= ");
				break;
			case LT:
				resultingFilter.append(" < ");
				break;
			case LE:
				resultingFilter.append(" <= ");
				break;
			}

			resultingFilter.append('?');

			if (columns != null) {
				columns.add(column);
			}

			if (values != null) {
				values.add(filter.getValue());
			}
		}

		return resultingFilter;
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
	public static StringBuilder getFilterSetString(String entity, Filter filterSet, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

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

			resultingFilterSet.append(getFilterString(entity, currentFilter, columns, values));
			for (int i=1; i<filterSet.getFilterItems().size(); i++) {

				currentFilter = filterSet.getFilterItems().get(i);

				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterString(entity, currentFilter, columns, values));

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
			resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));
			for (int i=1; i<filterSet.getFilters().size(); i++) {

				currentFilterSet = filterSet.getFilters().get(i);
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet;
	}

	/**
	 * Retorna una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @param metadata Metadata de la operación de la que se desea obtener el filtro.
	 * 
	 * @return una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @throws ServerException
	 */
	public static StringBuilder getExcludeDeletedString(Metadata metadata) throws ServerException {

		if (metadata.getIncludeDeleted() == null || !metadata.getIncludeDeleted()) {

			StringBuilder includeDeletedString = new StringBuilder();

			EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(metadata.getEntity());

			String deletedFieldName = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

			includeDeletedString.append(deletedFieldName).append(" IS NULL");

			return includeDeletedString;
		}

		return null;
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
				resultingOrdination = new StringBuilder();
			}
			else {
				resultingOrdination.append(", ");
			}

			resultingOrdination.append(orderAttribute.getName());

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
	 * Extrae el conjunto de datos del set de resultados y lo añade a la estructura de datos de la operación.   
	 * 
	 * @param resultSet Set de resultados del que se extrae la información.
	 * @param operation Operación en la que se añade el conjunto de datos extraido.
	 * 
	 * @return la operación poblada. 
	 * 
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public static Operation extractData(ResultSet resultSet, Operation operation) throws SQLException, DataAccessException {

		long numItems = 0;
		AbstractList<DataItem> data = operation.getData();

		if (data != null && data.size() > 0) {

			DataItem referenceDataItem = new DataItem(data.get(0));

			if (referenceDataItem != null && referenceDataItem.getAttributes() != null && referenceDataItem.getAttributes().size() > 0) {

				int itemIndex = 0;

				if (resultSet.first()) {

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

							Object value = resultSet.getObject(i++);

							dataItem.getAttributes().put(attributeAlias, resultSet.wasNull() ? null : value);
						}

						numItems++;
					}while(resultSet.next());
				}
			}
		}

		operation.getMetadata().setItems(numItems);

		return operation;
	}

	/**
	 * Recupera del subsistema las rows marcadas como eliminadas de la operación indicada. 
	 * 
	 * @param connection Conexión JDBC a usar para realizar la recuperación.
	 * @param dataItem Elemento que contiene información de la fila a recuperar y actualizar.
	 * @param entityAccessPolicy 
	 * @param entityInformation Información de la entidad en el subsistema. 
	 * 
	 * @return un array con los índices de las rows recuperadas dentro de la operación.
	 * 
	 * @throws SQLException 
	 * @throws ServerException 
	 */
	public static void recoverRowFromInsert(Connection connection, DataItem dataItem, EntityAccessPolicy entityAccessPolicy, EntityJDBCInformation entityInformation) throws SQLException, ServerException {

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

		recoverQuery.append(" SET ").append(revisionColumn).append("=?,").append(birthColumn).append("=?,").append(modifiedColumn).append("=?,").append(deletedColumn).append("=?");

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

					recoverQuery.append(',').append(attribute.getName()).append("=?");

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

		recoverQuery.append(" WHERE ").append(deletedColumn).append(" IS NOT NULL");

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

			for (EntityAttribute key : entityKey.getKey()) {

				Object value = dataItem.getAttributes().get(key.getAlias());

				if (value != null) {

					String columnName = entityAccessPolicy.getData().getAttribute(key.getAlias()).getName();

					if (keyQuery == null) {

						keyQuery = new StringBuilder("(").append(columnName).append("=?");
					}
					else {

						keyQuery.append(" AND ").append(columnName).append("=?");
					}

					fndTypes.add(entityInformation.dataFields.get(columnName));
					fndParams.add(value);
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
				preparedStatement.setObject(j, params.get(i), types.get(i));
			}
			
			for (int i=0; i<fndTypes.size(); i++, j++) {
				preparedStatement.setObject(j, fndParams.get(i), fndTypes.get(i));
			}

			int modifiedRows = preparedStatement.executeUpdate();

			if (modifiedRows != 1) {
				throw new ServerException(GenericService.ERROR_CREATE_DUPLICATE_ROW, String.format(GenericService.ERROR_CREATE_DUPLICATE_ROW_MESSAGE, "DML: " + recoverQuery + ". Valores: " + params.toString()));
			}
		}
		finally {
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		/*
		 * Recuperación de los datos del registro. 
		 */
		StringBuilder recoveredQuery = new StringBuilder("SELECT ");
		AbstractList<String> attributes = new ArrayList<String>();

		boolean firstField = true;
		for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

			for (EntityAttribute key : entityKey.getKey()) {

				Object value = dataItem.getAttributes().get(key.getAlias());

				if (value == null) {

					if (firstField) {
						recoveredQuery.append(entityAccessPolicy.getData().getAttribute(key.getAlias()).getName());
						firstField = false;
					}
					else {
						recoveredQuery.append(',').append(entityAccessPolicy.getData().getAttribute(key.getAlias()).getName());
					}
					
					attributes.add(key.getAlias());
				}
			}
		}

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

					dataItem.getAttributes().put(attributeAlias, resultSet.wasNull() ? null : value);
				}
			}
			else {
				throw new ServerException(GenericService.ERROR_CREATE_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_CREATE_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
			}

			if (resultSet.next()) {
				throw new ServerException(GenericService.ERROR_CREATE_REFURBISHED_ROW_LOST, String.format(GenericService.ERROR_CREATE_REFURBISHED_ROW_LOST_MESSAGE, "DML: " + recoveredQuery + ". Valores: " + params.toString()));
			}
		}
		finally {
			JDBCUtils.CloseQuietly(resultSet);
			JDBCUtils.CloseQuietly(preparedStatement);
		}
	}
}
