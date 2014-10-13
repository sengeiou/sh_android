package com.fav24.shootr.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Value("${dataSource.user}")
    private String dataSourceUser;
    @Value("${dataSource.password}")
    private String dataSourcePassword;
    @Value("${dataSource.driver}")
    private String dataSourceDriver;
    @Value("${dataSource.pool}")
    private String dataSourcePool;
    @Value("${dataSource.server}")
    private String dataSourceServerName;
    @Value("${dataSource.port}")
    private String dataSourcePortNumber;
    @Value("${dataSource.dbname}")
    private String dataSourceDatabaseName;

    /* Tunning */
    @Value("${dataSource.cachePrepStmts}")
    private String dataSourceCachePrepStmts;
    @Value("${dataSource.prepStmtCacheSize}")
    private String dataSourcePrepStmtCacheSize;
    @Value("${dataSource.prepStmtCacheSqlLimit}")
    private String dataSourcePrepStmtCacheSqlLimit;
    @Value("${dataSource.useServerPrepStmts}")
    private String dataSourceUseServerPrepStmts;


    @Bean
    public DataSource dataSource() {

        HikariConfig config = new HikariConfig();

        config.setUsername(this.dataSourceUser);
        config.setPassword(this.dataSourcePassword);

        config.setMaximumPoolSize(Integer.valueOf(this.dataSourcePool));

        config.addDataSourceProperty("serverName", this.dataSourceServerName);
        config.addDataSourceProperty("portNumber", this.dataSourcePortNumber);
        config.addDataSourceProperty("databaseName", this.dataSourceDatabaseName);
        /* Tunning properties -> https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration */
        config.addDataSourceProperty("cachePrepStmts", this.dataSourceCachePrepStmts);
        config.addDataSourceProperty("prepStmtCacheSize", this.dataSourcePrepStmtCacheSize);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", this.dataSourcePrepStmtCacheSqlLimit);
        config.addDataSourceProperty("useServerPrepStmts", this.dataSourceUseServerPrepStmts);

        config.addDataSourceProperty("characterEncoding", "UTF-8");


        config.setDataSourceClassName(this.dataSourceDriver);
        config.setPoolName("shootr-Pool");

        DataSource ds = new HikariDataSource(config);
        return ds;
    }


}