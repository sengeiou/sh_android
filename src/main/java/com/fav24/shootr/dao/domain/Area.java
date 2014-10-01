package com.fav24.shootr.dao.domain;


public class Area extends Synchronized{

    private Long idArea;
    private String name;

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

    @Override
    public String toString() {
        return "Area{" +
                "idArea=" + idArea +
                ", name='" + name + '\'' +
                '}';
    }
}
