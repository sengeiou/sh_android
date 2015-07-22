package com.shootr.android.ui.model;

import java.io.Serializable;

public class StreamModel implements Serializable {

    private String idStream;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String tag;
    private boolean amIAuthor;

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreamModel that = (StreamModel) o;

        if (!idStream.equals(that.idStream)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idStream.hashCode();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean amIAuthor() {
        return amIAuthor;
    }

    public void setAmIAuthor(boolean amIAuthor) {
        this.amIAuthor = amIAuthor;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }
}
