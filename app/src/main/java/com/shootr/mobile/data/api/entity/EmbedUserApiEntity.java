package com.shootr.mobile.data.api.entity;

public class EmbedUserApiEntity {

    private String idUser;
    private String userName;
    private String name;
    private String photo;
    private boolean strategic;

    private Long birth;
    private Long modified;
    private Integer revision;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getBirth() {
        return birth;
    }

    public void setBirth(Long birth) {
        this.birth = birth;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Boolean isStrategic() {
        return strategic;
    }

    public void setStrategic(Boolean strategic) {
        this.strategic = strategic;
    }
}
