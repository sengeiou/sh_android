package com.fav24.shootr.dao.impl;


import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.utils.PropertiesManager;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Types;
import java.util.List;

public class AreaDAOImpl extends BaseDAOImpl implements com.fav24.shootr.dao.AreaDAO {

    @Override
    public List<Area> getAllAreas(){
        String query = PropertiesManager.getProperty("area.select.all");
        List<Area> list = getJdbcTemplate().query(query, new Object[]{}, new BeanPropertyRowMapper<Area>(Area.class));
        return list;
    }

    @Override
    public Area getAreaByID(Long idArea){
        String query = PropertiesManager.getProperty("area.select.one.byid");
        List<Area> list = getJdbcTemplate().query(query, new Object[]{idArea}, new BeanPropertyRowMapper<Area>(Area.class));
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    public Long insertArea(Area area){
        String query = PropertiesManager.getProperty("area.insert");

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
        factory.setGeneratedKeysColumnNames(new String[]{"idArea"});
        factory.addParameter(new SqlParameter(Types.VARCHAR));//name
        factory.addParameter(new SqlParameter(Types.NUMERIC));//idAreaOpta

        Object[] params = new Object[]{
              area.getName(),
              area.getIdAreaOpta()
        };

        PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
        GeneratedKeyHolder gkf = new GeneratedKeyHolder();

        int result = getJdbcTemplate().update(psc, gkf);
        return (result > 0) ? new Long(gkf.getKey().longValue()) : -1;
    }

    @Override
    public long updateArea(Area area)  {
        String query = PropertiesManager.getProperty("area.update");
        return getJdbcTemplate().update(query, area.getName(), area.getIdAreaOpta(), area.getIdArea());
    }


}
