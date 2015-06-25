package com.shootr.android.ui.model;


public class EventModel {

    private String idEvent;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String tag;
    private boolean amIAuthor;

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
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

        EventModel that = (EventModel) o;

        if (!idEvent.equals(that.idEvent)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idEvent.hashCode();
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
