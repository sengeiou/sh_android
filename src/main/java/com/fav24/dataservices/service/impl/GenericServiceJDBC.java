package com.fav24.dataservices.service.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.fav24.dataservices.domain.Filter;
import com.fav24.dataservices.domain.FilterSet;
import com.fav24.dataservices.domain.Item;
import com.fav24.dataservices.domain.Key;
import com.fav24.dataservices.domain.Metadata;
import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;


/**
 * Versión JDBC de la interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class GenericServiceJDBC extends GenericServiceBasic {

	private TransactionTemplate transactionTemplate;
	private JdbcTemplate jdbcTemplate;

	/**
	 * Asigna la plantilla para la gestión de transacciones que usará este servicio.
	 * 
	 * @param transactionTemplate La plantilla para la gestión de transacciones a asignar.
	 */
	@Autowired
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	/**
	 * Asigna plantilla de acceso JDBC que usará este servicio.
	 * 
	 * @param jdbcTemplate La plantilla de acceso JDBC a asignar.
	 */
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean startTransaction() {
//		transactionTemplate.setIsolationLevel(isolationLevel);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean endTransaction(boolean commit) {
		return false;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de claves.
	 * @param keys Lista de claves a resolver.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	private String getKeyString(String entity, AbstractList<Key> keys) throws ServerException {

		StringBuilder resultingKey = new StringBuilder();

		Key key = keys.get(0);
		resultingKey.append(key.getName()).append('=').append(key.getValue());

		for (int i=1; i<keys.size(); i++) {
			key = keys.get(i);
			resultingKey.append(" AND ");
			resultingKey.append(key.getName()).append('=').append(key.getValue());
		}

		return resultingKey.toString();
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	private String getFilterString(String entity, Filter filter) throws ServerException {

		StringBuilder resultingFilter = new StringBuilder();

		resultingFilter.append(filter.getName());

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

		resultingFilter.append(filter.getValue());

		return resultingFilter.toString();
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	private String getFilterSetString(String entity, FilterSet filterSet) throws ServerException {

		if ((filterSet.getFilters() == null || filterSet.getFilters().size() == 0) &&
				(filterSet.getFilterSets() == null || filterSet.getFilterSets().size() == 0)) {

			return null;
		}

		StringBuilder resultingFilterSet = new StringBuilder();

		resultingFilterSet.append('(');

		/*
		 * Resolución de los filtros simples.
		 */
		if (filterSet.getFilters() != null && filterSet.getFilters().size() > 0) {
			Filter currentFilter = filterSet.getFilters().get(0);
			resultingFilterSet.append(getFilterString(entity, currentFilter));
			for (int i=1; i<filterSet.getFilters().size(); i++) {

				currentFilter = filterSet.getFilters().get(i);
				resultingFilterSet.append(filterSet.getNexus() == FilterSet.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterString(entity, currentFilter));

			}
		}

		/*
		 * Resolución de los conjuntos de filtros anidados.
		 */
		if (filterSet.getFilterSets() != null && filterSet.getFilterSets().size() > 0) {
			if (filterSet.getFilters() != null && filterSet.getFilters().size() > 0) {
				resultingFilterSet.append(filterSet.getNexus() == FilterSet.NexusType.AND ? " AND " : " OR ");
			}
			FilterSet currentFilterSet = filterSet.getFilterSets().get(0);
			resultingFilterSet.append(getFilterSetString(entity, currentFilterSet));
			for (int i=1; i<filterSet.getFilters().size(); i++) {

				currentFilterSet = filterSet.getFilterSets().get(i);
				resultingFilterSet.append(filterSet.getNexus() == FilterSet.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entity, currentFilterSet));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet.toString();
	}

	/**
	 * Retorna la operación indicada, con el conjunto de resultados asociado.
	 * 
	 * @param operation Operación a enriquecer.
	 * @param resultSet Conjunto de resultados a usar.
	 * 
	 * @return la operación indicada, con el conjunto de resultados asociado.
	 */
	private Operation populateResultSet(Operation operation, List<Map<String, Object>> resultSet) {

		AbstractList<Item> data = operation.getData();

		if (resultSet == null || resultSet.isEmpty()) {
			operation.getMetadata().setItems(0L);
		}
		else {
			operation.getMetadata().setItems((long) resultSet.size());
		}

		if (data == null) {
			data = new ArrayList<Item>(resultSet.size());
			operation.setData(data);
		}
		else {
			data.clear();
		}

		for (Map<String, Object> row : resultSet) {

			Map<String, Object> itemAtributes = new HashMap<String, Object>();
			Item item = new Item();
			item.setAttributes(itemAtributes);

			for (Entry<String, Object> attribute : row.entrySet()) {

				if ("csys_birth".equals(attribute.getKey())) {
					if (attribute.getValue() != null) {
						item.setBirthDate(((Date)attribute.getValue()).getTime());
					}
				}
				else if ("csys_modified".equals(attribute.getKey())) {
					if (attribute.getValue() != null) {
						item.setUpdateDate(((Date)attribute.getValue()).getTime());
					}
				}
				else if ("csys_deleted".equals(attribute.getKey())) {
					if (attribute.getValue() != null) {
						item.setDeleteDate(((Date)attribute.getValue()).getTime());
					}
				}
				else  {
					itemAtributes.put(attribute.getKey(), attribute.getValue());
				}
			}

			data.add(item);
		}

		return operation;
	}

	private ResultSetExtractor<Item> getResultSetExtractor(Item item) {
		return null;
	}
	 
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation retreave(Requestor requestor, Operation operation) throws ServerException {

		final StringBuilder query = new StringBuilder();

		query.append("SELECT ");

		/*
		 * Especificación del conjunto de campos de la query.
		 */
		if (operation.getData() != null && operation.getData().size() > 0) {

			Item item = operation.getData().get(0);
			query.append("csys_birth,");
			query.append("csys_modified,");
			query.append("csys_deleted,");
			query.append("csys_revision");

			for (String attribute : item.getAttributes().keySet()) {

				if (!Metadata.InternalAttribute.contains(attribute)) {
					query.append(',').append(attribute);
				}
			}
		}
		else {
			query.append('*');
		}

		/*
		 * Especificación de la tabla.
		 */
		query.append(" FROM ").append(operation.getMetadata().getEntity());

		/*
		 * Especificación del filtro.
		 */
		if (operation.getMetadata().getKey() != null) {
			query.append(" WHERE ").append(getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey()));
		}
		else if (operation.getMetadata().getFilter() != null) {
			query.append(" WHERE ").append(getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter()));
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_REQUEST, "Es necesario indicar KEY o FILTER para la resolución de esta petición.");
		}

		/*
		 * Ejecución de la sentencia.
		 */
		//use TransactionCallback handler if some result is returned
		
/*
		transactionTemplate.execute(new TransactionCallback<Integer>() {

			public Integer doInTransaction(TransactionStatus paramTransactionStatus) {

				Object[] params = new Object[]{user.getUserName(), user.getPassword(), user.isEnabled(), user.getId()};
				int[] types = new int[]{Types.VARCHAR,Types.VARCHAR,Types.BIT,Types.INTEGER};

				return jdbcTemplate.query(sql, args, rowMapper)(query.toString(), params, types);
			}
		});
*/
		List<Map<String, Object>> resultSet = null; //jdbcTemplate.queryForList(query.toString());

		/*
		 * Poblado de resultados.
		 */
		populateResultSet(operation, resultSet);

		return operation;
	}
}
