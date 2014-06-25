package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAttribute;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.domain.policy.EntityFilter;
import com.fav24.dataservices.domain.policy.EntityKey;
import com.fav24.dataservices.domain.policy.EntityOrderAttribute;
import com.fav24.dataservices.domain.policy.EntityDataAttribute.Direction;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.util.JDBCUtils;


@Scope("singleton")
@Component
public class GenericServiceDataSourceInfoJDBC extends GenericServiceDataSourceInfo {


	/**
	 * Chequea las políticas de acceso definidas, contra la fuente de datos a la que ataca el servicio.
	 * Obtiene toda la información necesaria de la fuente de datos, para resolver las peticiones.
	 * 
	 * Los chequeos que realiza son:
	 * 	- Que las entidades existen en la fuente de datos.
	 *  - Que los attributos de cada una de las entidades, están disponibles en la fuente de datos.
	 *  - Que el conjunto de claves especificado en las políticas de acceso, existe en la fuente de datos.
	 *  - Que el conjunto de filtros especificado en las políticas de acceso, se corresponde con su correspondiente conjunto de índices en la fuente de datos.
	 *  
	 * Nota: En este punto, es posible aprovechar el acceso a los metadatos de las entidades de la fuente de datos, para obtener el tipo de dato asociado a cada atributo de dato, clave o filtro.
	 * 
	 * @param accessPolicy Política de acceso a chequear.
	 * 
	 * @return toda la información necesaria de la fuente de datos, para resolver las peticiones.
	 *  
	 * @throws ServerException
	 */
	public Map<String, EntityDataSourceInfo> checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy accessPolicy) throws ServerException {

		if (accessPolicy == null) {
			return null;
		}

		Map<String, EntityDataSourceInfo> entitiesInformation = new LinkedHashMap<String, EntityDataSourceInfo>();

		javax.sql.DataSource dataSource = DataSources.getDataSourceDataService();
		Connection connection = null;

		try {
			connection = dataSource.getConnection();

			setProduct(JDBCUtils.getProduct(connection));

			// Se recorren las entidades.
			for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {

				// Las entidades virtuales no existen en el subsistema.
				if (entityAccessPolicy.isVirtual()) {
					continue;
				}

				// Entidad.
				EntityDataSourceInfo entityJDBCInformation = new EntityDataSourceInfo();
				String table = entityAccessPolicy.getName().getName();
				ResultSet tables = null;
				try {

					tables = connection.getMetaData().getTables(null, null, table, null);

					if (tables.first()) {

						entityJDBCInformation.catalog = tables.getString("TABLE_CAT");
						if (tables.wasNull()) {
							entityJDBCInformation.catalog = null;
						}
						entityJDBCInformation.schema = tables.getString("TABLE_SCHEM");
						if (tables.wasNull()) {
							entityJDBCInformation.schema = null;
						}
						entityJDBCInformation.name = tables.getString("TABLE_NAME");
						entityJDBCInformation.isView = !tables.getString("TABLE_TYPE").equals("TABLE"); // si es distinto de "TABLE", los atributos de datos únicamente pueden ser de lectura.

						entitiesInformation.put(entityJDBCInformation.name, entityJDBCInformation);
					}
					else {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La tabla <%s> no existe en la fuente de datos.", table));
					}
				}
				catch(Exception e) {
					throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de la tabla <%s>, debido a: %s", table, e.getMessage()));
				}
				finally {
					JDBCUtils.CloseQuietly(tables);
				}

				// Claves primárias de la entidad.
				if (!entityJDBCInformation.isView) {
					ResultSet primaryKeys = null;

					try {

						// Obtención de las columnas que conforman la clave primaria.
						primaryKeys = connection.getMetaData().getPrimaryKeys(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name);

						if (primaryKeys.first()) {
							entityJDBCInformation.primaryKey = new TreeMap<String, Integer>();

							do {
								entityJDBCInformation.primaryKey.put(primaryKeys.getString("COLUMN_NAME"), null);
							} while(primaryKeys.next());
						}
						else {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener la clave primaria de la tabla <%s>.", entityJDBCInformation.name));
						}
					}
					catch(Exception e) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener la clave primaria de la tabla <%s>, debido a: %s", entityJDBCInformation.name, e.getMessage()));
					}
					finally {
						JDBCUtils.CloseQuietly(primaryKeys);
					}

					// Obtención de los tipos de datos de las columnas que conforman la clave primaria.
					if (entityJDBCInformation.primaryKey != null) {

						ResultSet columns = null;

						try {
							columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);

							if (columns.first()) {

								do {

									String columnName = columns.getString("COLUMN_NAME");

									if (entityJDBCInformation.primaryKey.containsKey(columnName)) {

										entityJDBCInformation.primaryKey.put(columnName, columns.getInt("DATA_TYPE"));
									}

								} while(columns.next());
							}
							else {
								throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener los tipos de datos de las columnas de la clave primaria de la tabla <%s>.", entityJDBCInformation.name));
							}
						}
						catch(Exception e) {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener los tipos de datos de las columnas de la clave primaria de la tabla <%s>, debido a: %s" + entityJDBCInformation.name, e.getMessage()));
						}
						finally {
							JDBCUtils.CloseQuietly(columns);
						}
					}
				}

				// Atributos de datos de la entidad.
				if (entityAccessPolicy.getData() != null && entityAccessPolicy.getData().getData() != null && entityAccessPolicy.getData().getData().size() > 0) {

					entityJDBCInformation.dataFields = new TreeMap<String, Integer>();
					entityJDBCInformation.dataFieldsDefaults = new TreeMap<String, Object>();
					entityJDBCInformation.generatedData = new HashSet<String>();
					ResultSet columns = null;

					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);

						if (columns.first()) {
							do {

								String columnName = columns.getString("COLUMN_NAME");

								if (columns.getBoolean("IS_AUTOINCREMENT") || columns.getBoolean("IS_GENERATEDCOLUMN")) {
									entityJDBCInformation.generatedData.add(columnName);
								}

								for (EntityDataAttribute entityDataAttribute : entityAccessPolicy.getData().getData()) {

									if (entityDataAttribute.getName().equalsIgnoreCase(columnName)) {

										if (!entityDataAttribute.getName().equals(columnName)) {
											throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La columa <%s> de la tabla <%s> difiere en mayúsculas y/o minúsculas con el nombre del atributo <%s>.", columnName, table, entityDataAttribute.getName()));
										}

										if (entityJDBCInformation.isView && entityDataAttribute.getDirection() != Direction.OUTPUT) {
											throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La columa <%s> de la tabla <%s> es de solo lectura.", columnName, table));
										}

										entityJDBCInformation.dataFields.put(columnName, columns.getInt("DATA_TYPE"));
										entityJDBCInformation.dataFieldsDefaults.put(columnName, columns.getObject("COLUMN_DEF"));

										break;
									}
								}

							} while(columns.next());
						}
						else {
							JDBCUtils.CloseQuietly(columns);

							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado columnas para la tabla <%s>.", table));
						}
					}
					catch(Exception e) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de la tabla <%s>, debido a: %s", table, e.getMessage()));
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
					}

					// Error, hay campos que no se han podido encontrar el la tabla.
					if (entityJDBCInformation.dataFields.size() < entityAccessPolicy.getData().getData().size()) {

						StringBuilder lostFields = null;

						for (EntityDataAttribute entityDataAttribute : entityAccessPolicy.getData().getData()) {

							String fieldName = entityDataAttribute.getName();

							boolean found = false;
							for (Entry<String, Integer> field : entityJDBCInformation.dataFields.entrySet()) {

								if (fieldName.equalsIgnoreCase(field.getKey())) {
									found = true;
									break;
								}
							}

							if (!found) {
								if (lostFields == null) {
									lostFields = new StringBuilder();
								}
								else {
									lostFields.append(", ");	
								}

								lostFields.append(fieldName);
							}
						}

						if (lostFields != null) {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas <%s> para la tabla <%s>.", lostFields.toString(), table));
						}
					}
				}

				// Atributos de las claves de la entidad.
				if (entityAccessPolicy.getKeys() != null && entityAccessPolicy.getKeys().getKeys() != null && entityAccessPolicy.getKeys().getKeys().size() > 0) {

					entityJDBCInformation.keyFields = new TreeMap<String, Integer>();

					if (entityJDBCInformation.isView) {

						for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

							for (EntityAttribute keyAttribute : entityKey.getKey()) {
								entityJDBCInformation.keyFields.put(keyAttribute.getName(), null);
							}
						}
					}
					else {

						entityJDBCInformation.keys = new HashMap<String, Set<String>>();

						//Índices únicos.
						ResultSet uniqueIndexes = null;

						try {
							uniqueIndexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, true, false);

							if (uniqueIndexes.first()) {
								do {
									String columnName = uniqueIndexes.getString("COLUMN_NAME");
									String indexName = uniqueIndexes.getString("INDEX_NAME");
									if (uniqueIndexes.wasNull()) {
										indexName = null;
									}

									Set<String> indexFields = entityJDBCInformation.keys.get(indexName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.keys.put(indexName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.keyFields.put(columnName, null);

								} while(uniqueIndexes.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las claves de la tabla <%s>, debido a: %s", table, e.getMessage()));
						}
						finally {
							JDBCUtils.CloseQuietly(uniqueIndexes);
						}

						for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

							if (!hasEquivalentAttributeCollection(entityJDBCInformation.keys, entityKey.getKey())) {

								StringBuilder specificMessage = new StringBuilder(" Clave no permitida: La tabla ").append(table).
										append(" no tiene definida la clave única o índice único para los campos ").append(entityKey.getKeyNamesString()).append(".");
								specificMessage.append("\nLas claves disponibles son:\n");

								for(Set<String> keyFields : entityJDBCInformation.keys.values()) {

									specificMessage.append('<');
									boolean firstField = true;
									for(String keyField : keyFields) {

										if (!firstField) {
											specificMessage.append(", ");
										}

										specificMessage.append(keyField);
										firstField = false;
									}
									specificMessage.append('>');
								}

								throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + specificMessage);
							}
						}
					}

					ResultSet columns = null;
					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
						if (columns.first()) {
							do {
								String columnName = columns.getString("COLUMN_NAME");
								int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

								if (entityJDBCInformation.keyFields.containsKey(columnName)) {
									entityJDBCInformation.keyFields.put(columnName, columnType);
								}

							} while(columns.next());
						}
					}
					catch(Exception e) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de las claves de la tabla <%s>, debido a: %s", table, e.getMessage()));
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
					}

					// Comprobación de que los campos clave están correctamente definidos en la base de datos.
					StringBuilder lostKeyFields = null;
					for (Entry<String, Integer> field : entityJDBCInformation.keyFields.entrySet()) {

						if (field.getValue() == null) {
							if (lostKeyFields != null) {
								lostKeyFields.append(", ");	
								lostKeyFields.append(field.getKey());
							}
							else {
								lostKeyFields = new StringBuilder(field.getKey());
							}
						}
					}

					if (lostKeyFields != null) {

						StringBuilder specificMessage = new StringBuilder(" No se han encontrado las columnas clave ").append(lostKeyFields).append(" para la tabla ").append(table);
						specificMessage.append("\nLas claves disponibles son:\n");

						for(Set<String> keyFields : entityJDBCInformation.keys.values()) {

							specificMessage.append('<');
							boolean firstField = true;
							for(String keyField : keyFields) {

								if (!firstField) {
									specificMessage.append(", ");
								}

								specificMessage.append(keyField);
								firstField = false;
							}
							specificMessage.append('>');
						}

						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas clave <%s> para la tabla <%s>.", lostKeyFields, table));
					}
				}

				// Atributos de los filtros de la entidad.
				if (entityAccessPolicy.getFilters() != null && entityAccessPolicy.getFilters().getFilters() != null && entityAccessPolicy.getFilters().getFilters().size() > 0) {

					entityJDBCInformation.filterFields = new TreeMap<String, Integer>();

					if (entityJDBCInformation.isView) {

						for (EntityAttribute filterAttribute : entityAccessPolicy.getFilters().getAllFiltersAttributes(null)) {

							entityJDBCInformation.filterFields.put(filterAttribute.getName(), null);
						}
					}
					else {

						entityJDBCInformation.indexes = new HashMap<String, Set<String>>();

						//Índices.
						ResultSet indexes = null;

						try {
							indexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, false, false);
							if (indexes.first()) {
								do {
									String columnName = indexes.getString("COLUMN_NAME");
									String indexName = indexes.getString("INDEX_NAME");
									if (indexes.wasNull()) {
										indexName = null;
									}

									Set<String> indexFields = entityJDBCInformation.indexes.get(indexName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.indexes.put(indexName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.filterFields.put(columnName, null);

								} while(indexes.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de los índices de la tabla <%s>, debido a: %s", table, e.getMessage()));
						}
						finally {
							JDBCUtils.CloseQuietly(indexes);
						}

						//Foreing keys
						ResultSet importedKeys = null;

						try {
							importedKeys = connection.getMetaData().getImportedKeys(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name);
							if (importedKeys.first()) {
								do {
									String columnName = importedKeys.getString("FKCOLUMN_NAME");
									String foreingKeyName = importedKeys.getString("FK_NAME");
									if (importedKeys.wasNull()) {
										foreingKeyName = null;
									}

									Set<String> indexFields = entityJDBCInformation.indexes.get(foreingKeyName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.indexes.put(foreingKeyName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.filterFields.put(columnName, null);

								} while(importedKeys.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las claves foraneas importadas de la tabla <%s>, debido a: ", table, e.getMessage()));
						}
						finally {
							JDBCUtils.CloseQuietly(importedKeys);
						}

						for (EntityFilter entityFilter : entityAccessPolicy.getFilters().getFilters()) {

							if (!hasEquivalentAttributeCollection(entityJDBCInformation.indexes, entityFilter.getFilter())) {
								throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " Filtro no permitido: La tabla <%s> no tiene definido un índice para los campos <%s>.", table, entityFilter.getFilterNamesString()));
							}
						}
					}

					ResultSet columns = null;

					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
						if (columns.first()) {
							do {
								String columnName = columns.getString("COLUMN_NAME");
								int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

								if (entityJDBCInformation.filterFields.containsKey(columnName)) {
									entityJDBCInformation.filterFields.put(columnName, columnType);
								}

							} while(columns.next());
						}
					}
					catch(SQLException e) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de los filtros definidos para la tabla <%s>.", table));
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
					}

					// Comprobación de que los campos clave están correctamente definidos en la base de datos.
					StringBuilder lostFilterFields = null;
					for (Entry<String, Integer> field : entityJDBCInformation.filterFields.entrySet()) {

						if (field.getValue() == null) {
							if (lostFilterFields != null) {
								lostFilterFields.append(", ");	
								lostFilterFields.append(field.getKey());
							}
							else {
								lostFilterFields = new StringBuilder(field.getKey());
							}
						}
					}

					if (lostFilterFields != null) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas de filtrado <%s> para la tabla <%s>.", lostFilterFields.toString(), table));
					}
				}

				// Atributos de las ordenaciones de la entidad.
				if (entityAccessPolicy.getOrdination() != null && entityAccessPolicy.getOrdination().getOrder().size() > 0) {

					AbstractList<EntityOrderAttribute> order = entityAccessPolicy.getOrdination().getOrder();

					StringBuilder illegalAttributes = null;

					for(EntityOrderAttribute orderAttribute : order) {

						if (!entityAccessPolicy.getData().hasAttribute(orderAttribute.getAlias())) {

							if (illegalAttributes == null) {
								illegalAttributes = new StringBuilder(orderAttribute.getAlias());
							}
							else {
								illegalAttributes.append(", ").append(orderAttribute.getAlias());	
							}
						}
					}

					if (illegalAttributes != null) {
						throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han definido los atributos correspondientes en la sección de datos, para los atributos de ordenación <%s> de la entidad <%s>.", illegalAttributes.toString(), entityAccessPolicy.getName().getAlias()));
					}
				}
			}
		} 
		catch (ServerException e) {

			if (entitiesInformation != null && accessPolicy != null) {

				for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {
					entitiesInformation.remove(entityAccessPolicy.getName().getName());
				}
			}

			throw e;
		}
		catch (Throwable e) {

			if (entitiesInformation != null && accessPolicy != null) {

				for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {
					entitiesInformation.remove(entityAccessPolicy.getName().getName());
				}
			}

			throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, String.format(ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener los metadatos de la fuente de datos, debido a: %s", e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(connection);
		}

		return entitiesInformation;
	}
}
