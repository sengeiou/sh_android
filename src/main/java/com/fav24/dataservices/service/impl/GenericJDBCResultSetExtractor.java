package com.fav24.dataservices.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.fav24.dataservices.domain.Operation;

public class GenericJDBCResultSetExtractor implements ResultSetExtractor<Operation> {

	private Operation operation;

	/**
	 * Constructor con parámetro.
	 * 
	 * @param operation Operación a popular.
	 */
	public GenericJDBCResultSetExtractor(Operation operation) {
		this.operation = operation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Operation extractData(ResultSet rs) throws SQLException, DataAccessException {

		
		
		return operation;
	}
}
