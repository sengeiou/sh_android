package com.shootr.mobile.domain;

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
    private String shortTitle;
    private String description;
    private String topic;
    private String country;
    private Integer mediaCount;
    private boolean removed;
    private List<User> watchers;
    private Integer totalFavorites;
    private Integer totalWatchers;
    private Long historicWatchers;
    private Long totalShots;
    private Long uniqueShots;

    public Boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public List<User> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<User> watchers) {
        this.watchers = watchers;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;

        Stream stream = (Stream) o;

        if (id != null ? !id.equals(stream.id) : stream.id != null) return false;
        if (authorId != null ? !authorId.equals(stream.authorId) : stream.authorId != null) return false;
        if (authorUsername != null ? !authorUsername.equals(stream.authorUsername)
          : stream.authorUsername != null) {
            return false;
        }
        if (title != null ? !title.equals(stream.title) : stream.title != null) return false;
        if (picture != null ? !picture.equals(stream.picture) : stream.picture != null) return false;
        if (shortTitle != null ? !shortTitle.equals(stream.shortTitle) : stream.shortTitle != null) return false;
        if (description != null ? !description.equals(stream.description) : stream.description != null) {
            return false;
        }
        return !(country != null ? !country.equals(stream.country) : stream.country != null);
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (authorUsername != null ? authorUsername.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (shortTitle != null ? shortTitle.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
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

    public Integer getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Integer getTotalWatchers() {
        return totalWatchers;
    }

    public void setTotalWatchers(Integer totalWatchers) {
        this.totalWatchers = totalWatchers;
    }

    public Long getHistoricWatchers() {
        return historicWatchers;
    }

    public void setHistoricWatchers(Long historicWatchers) {
        this.historicWatchers = historicWatchers;
    }

    public Long getTotalShots() {
        return totalShots;
    }

    public void setTotalShots(Long totalShots) {
        this.totalShots = totalShots;
    }

    public Long getUniqueShots() {
        return uniqueShots;
    }

    public void setUniqueShots(Long uniqueShots) {
        this.uniqueShots = uniqueShots;
    }

    public static class StreamExplicitComparator implements Comparator<Stream> {

        private Map<String, Integer> indexMap = new HashMap<>();

        public StreamExplicitComparator(List<String> orderedStreamIds) {
            for (int i = 0; i < orderedStreamIds.size(); i++) {
                String id = orderedStreamIds.get(i);
                indexMap.put(id, i);
            }
        }

        @Override public int compare(Stream stream1, Stream stream2) {
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

        @Override public int compare(Stream left, Stream right) {
            return left.getTitle().compareTo(right.getTitle());
        }
    }
}
