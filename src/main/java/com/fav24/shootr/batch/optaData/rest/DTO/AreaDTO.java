package com.fav24.shootr.batch.optaData.rest.DTO;


public class AreaDTO {

    private Long area_id;
    private String countrycode;
    private String name;

    public AreaDTO(Long area_id, String countrycode, String name) {
        this.area_id = area_id;
        this.countrycode = countrycode;
        this.name = name;
    }

    public AreaDTO() {

    }

    public Long getArea_id() {
        return area_id;
    }

    public void setArea_id(Long area_id) {
        this.area_id = area_id;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "AreaDTO{" +
                "area_id=" + area_id +
                ", countrycode='" + countrycode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
