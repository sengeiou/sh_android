package com.fav24.shootr.dao;

import javax.sql.DataSource;
import java.util.Date;

public interface BaseDAO {

    public void setDs(DataSource dataSource);

    //test connection
    public Date getDate();
}
