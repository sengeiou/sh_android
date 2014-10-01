package com.fav24.shootr.dao.domain;


public class Area extends Synchronized{

    private Long idArea;
    private String name;
    private String countrycode;

    public Area() {
    }

    public Area(Long idArea, String name, String countrycode) {
        this.idArea = idArea;
        this.name = name;
        this.countrycode = countrycode;
    }

    public Long getIdArea() {
        return idArea;
    }

    public void setIdArea(Long idArea) {
        this.idArea = idArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    @Override
    public String toString() {
        return "Area{" +
                "idArea=" + idArea +
                ", name='" + name + '\'' +
                ", countrycode='" + countrycode + '\'' +
                '}';
    }
}
