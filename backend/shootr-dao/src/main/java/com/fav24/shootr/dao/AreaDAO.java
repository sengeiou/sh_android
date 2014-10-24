package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.Area;

import java.util.List;


public interface AreaDAO extends BaseDAO {

    /**
     * Return
     * @return
     */
    List<Area> getAllAreas();

    /**
     * Given an idArea, returns the area that match with idArea
     * @param idArea Team identifier
     * @return Team that match with given idArea
     */
    Area getAreaByID(Long idArea);

    /**
     * Given an Team Object, this method insert it into DataBase and returns the generated idArea
     * @param area object to insert
     * @return generated idArea of the inserted area
     */
    Long insertArea(Area area);

    /**
     * Given an Team Object, this method update it into DataBase and returns the number of the updated rows
     * @param area object to update
     * @return number of the updated rows
     */
    long updateArea(Area area);
}
