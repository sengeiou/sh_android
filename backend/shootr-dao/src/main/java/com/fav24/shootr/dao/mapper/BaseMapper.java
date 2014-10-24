package com.fav24.shootr.dao.mapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class BaseMapper {
    /**
     * Comprueba que el ResultSet tiene la columna
     *
     * @param metaData
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    private static boolean hasColumn(ResultSetMetaData metaData, String columnName) throws SQLException {
        int columns = metaData.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnName(x)) || columnName.equalsIgnoreCase(metaData.getColumnLabel(x))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recupera un Long si existe en el ResultSet
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static Long getLong(ResultSet rs, String columnName) throws SQLException {
        Long l = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            l = rs.getLong(columnName);
            if (rs.wasNull()) l = null;
        }
        return l;
    }

    /**
     * Recupera un Integer si existe en el ResultSet
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        Integer i = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            i = rs.getInt(columnName);
            if (rs.wasNull()) i = null;
        }
        return i;
    }

    /**
     * Recupera un String si existe en el ResultSet
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static String getString(ResultSet rs, String columnName) throws SQLException {
        String i = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            i = rs.getString(columnName);
            if (rs.wasNull()) i = null;
        }
        return i;
    }

    /**
     * Recupera un BigDecimal si existe en el ResultSet
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException {
        BigDecimal bd = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            bd = rs.getBigDecimal(columnName);
            if (rs.wasNull()) bd = null;
        }
        return bd;
    }

    /**
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static Double getDouble(ResultSet rs, String columnName) throws SQLException {
        Double bd = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            bd = rs.getDouble(columnName);
            if (rs.wasNull()) bd = null;
        }
        return bd;
    }

    /**
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static Float getFloat(ResultSet rs, String columnName) throws SQLException {
        Float bd = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            bd = rs.getFloat(columnName);
            if (rs.wasNull()) bd = null;
        }
        return bd;
    }

    /**
     * Recupera un Date si existe en el ResultSet
     *
     * @param rs
     * @param columnName
     * @return
     * @throws java.sql.SQLException
     */
    public static Date getDate(ResultSet rs, String columnName) throws SQLException {
        Date bd = null;
        if (hasColumn(rs.getMetaData(), columnName)) {
            Timestamp dt = rs.getTimestamp(columnName);
            if (dt != null && !rs.wasNull()) {
                bd = new Date(dt.getTime());
            }
        }
        return bd;
    }

}
