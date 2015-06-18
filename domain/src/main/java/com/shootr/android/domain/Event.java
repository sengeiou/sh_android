package com.shootr.android.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    private String id;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String tag;
    private String locale;

    public Event() {
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
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
        if (tag != null ? !tag.equals(event.tag) : event.tag != null) return false;
        return !(locale != null ? !locale.equals(event.locale) : event.locale != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (authorUsername != null ? authorUsername.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
          "id=" + id +
          ", title='" + title + '\'' +
          '}';
    }

    public static class EventExplicitComparator implements Comparator<Event> {

        private Map<String, Integer> indexMap = new HashMap<>();

        public EventExplicitComparator(List<String> orderedEventIds) {
            for (int i = 0; i < orderedEventIds.size(); i++) {
                String id = orderedEventIds.get(i);
                indexMap.put(id, i);
            }
        }

        @Override
        public int compare(Event ev1, Event ev2) {
            return rank(ev1) - rank(ev2);
        }

        private int rank(Event event) {
            Integer rank = indexMap.get(event.getId());
            if (rank == null) {
                throw new IllegalStateException(String.format("Event id not found in explicit list: %s",
                  event.toString()));
            }
            return rank;
        }
    }

    public static class EventNameComparator implements Comparator<Event> {

        @Override
        public int compare(Event left, Event right) {
            return left.getTitle().compareTo(right.getTitle());
        }
    }
}
