package com.shootr.android.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stream {

    private String id;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String tag;
    private String description;
    private String locale;
    private Integer mediaCount;

    public Stream() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;

        Stream stream = (Stream) o;

        if (id != null ? !id.equals(stream.id) : stream.id != null) return false;
        if (authorId != null ? !authorId.equals(stream.authorId) : stream.authorId != null) return false;
        if (authorUsername != null ? !authorUsername.equals(stream.authorUsername) : stream.authorUsername != null) {
            return false;
        }
        if (title != null ? !title.equals(stream.title) : stream.title != null) return false;
        if (picture != null ? !picture.equals(stream.picture) : stream.picture != null) return false;
        if (tag != null ? !tag.equals(stream.tag) : stream.tag != null) return false;
        if (description != null ? !description.equals(stream.description) : stream.description != null) return false;
        return !(locale != null ? !locale.equals(stream.locale) : stream.locale != null);
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (authorUsername != null ? authorUsername.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stream{" +
          "id=" + id +
          ", title='" + title + '\'' +
          '}';
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public static class StreamExplicitComparator implements Comparator<Stream> {

        private Map<String, Integer> indexMap = new HashMap<>();

        public StreamExplicitComparator(List<String> orderedStreamIds) {
            for (int i = 0; i < orderedStreamIds.size(); i++) {
                String id = orderedStreamIds.get(i);
                indexMap.put(id, i);
            }
        }

        @Override
        public int compare(Stream stream1, Stream stream2) {
            return rank(stream1) - rank(stream2);
        }

        private int rank(Stream stream) {
            Integer rank = indexMap.get(stream.getId());
            if (rank == null) {
                throw new IllegalStateException(String.format("Stream id not found in explicit list: %s",
                  stream.toString()));
            }
            return rank;
        }
    }

    public static class StreamNameComparator implements Comparator<Stream> {

        @Override
        public int compare(Stream left, Stream right) {
            return left.getTitle().compareTo(right.getTitle());
        }
    }
}
