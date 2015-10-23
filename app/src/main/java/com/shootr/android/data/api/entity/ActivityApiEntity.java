package com.shootr.android.data.api.entity;

import com.shootr.android.data.entity.StreamEntity;

public class ActivityApiEntity {

    private String idActivity;

    private String idUser;
    private String username;

    private String idStream;
    private String streamTitle;
    private String streamShortTitle;

    private String comment;
    private String type;

    private String idShot;

    private Long birth;

    private Long modified;
    private Integer revision;

    private EmbedUserApiEntity user;
    private EmbedUserApiEntity targetUser;
    private StreamEntity stream;

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getStreamShortTitle() {
        return streamShortTitle;
    }

    public void setStreamShortTitle(String streamShortTitle) {
        this.streamShortTitle = streamShortTitle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public EmbedUserApiEntity getUser() {
        return user;
    }

    public void setUser(EmbedUserApiEntity user) {
        this.user = user;
    }

    public StreamEntity getStream() {
        return stream;
    }

    public void setStream(StreamEntity stream) {
        this.stream = stream;
    }

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public EmbedUserApiEntity getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(EmbedUserApiEntity targetUser) {
        this.targetUser = targetUser;
    }
}
