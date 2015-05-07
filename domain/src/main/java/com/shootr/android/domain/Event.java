package com.shootr.android.domain;

import java.util.Date;

public class Event {

    private String id;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String timezone;
    private String tag;
    private Date startDate;

    public Event() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (authorId != null ? !authorId.equals(event.authorId) : event.authorId != null) return false;
        if (authorUsername != null ? !authorUsername.equals(event.authorUsername) : event.authorUsername != null) {
            return false;
        }
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (picture != null ? !picture.equals(event.picture) : event.picture != null) return false;
        if (timezone != null ? !timezone.equals(event.timezone) : event.timezone != null) return false;
        if (tag != null ? !tag.equals(event.tag) : event.tag != null) return false;
        return !(startDate != null ? !startDate.equals(event.startDate) : event.startDate != null);
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (authorUsername != null ? authorUsername.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Event{" +
          "id=" + id +
          ", title='" + title + '\'' +
          '}';
    }
}
