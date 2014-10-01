package com.fav24.shootr.dao.impl;


import com.fav24.shootr.dao.BaseDAO;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.util.Date;

public class BaseDAOImpl extends JdbcDaoSupport implements BaseDAO {

    protected static Logger logger = Logger.getLogger(BaseDAOImpl.class);

    /*
    * wrapper del jdbcTemplate para las queries que necesiten un tratamiento especial con
    * un número de parametros indefinidos
    */
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setDs(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());

    }

    public Date getDate() {
        return getJdbcTemplate().queryForObject("select now() as date", Date.class);

    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }


}
