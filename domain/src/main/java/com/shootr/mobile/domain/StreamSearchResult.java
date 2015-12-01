package com.shootr.mobile.domain;

public class StreamSearchResult {

    private Stream stream;
    private int followingWatchersNumber;
    private boolean isWatching;
    private int favoritesNumber;

    public StreamSearchResult() {
    }

    public StreamSearchResult(Stream stream, int followingWatchersNumber) {
        this.stream = stream;
        this.followingWatchersNumber = followingWatchersNumber;
    }

    public StreamSearchResult(Stream stream, Integer followingWatchersNumber) {
        this.stream = stream;
        this.followingWatchersNumber = followingWatchersNumber != null ? followingWatchersNumber : 0;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public int getFollowingWatchersNumber() {
        return followingWatchersNumber;
    }

    public void setFollowingWatchersNumber(int followingWatchersNumber) {
        this.followingWatchersNumber = followingWatchersNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamSearchResult)) return false;

        StreamSearchResult that = (StreamSearchResult) o;

        if (followingWatchersNumber != that.followingWatchersNumber) return false;
        return stream.equals(that.stream);
    }

    @Override public int hashCode() {
        int result = stream.hashCode();
        result = 31 * result + followingWatchersNumber;
        return result;
    }

    @Override public String toString() {
        return "StreamSearchResult{" +
          "stream=" + stream +
          ", followingWatchersNumber=" + followingWatchersNumber +
          '}';
    }

    public boolean isWatching() {
        return isWatching;
    }

    public void setIsWatching(boolean isWatching) {
        this.isWatching = isWatching;
    }

    public int getFavoritesNumber() {
        return favoritesNumber;
    }

    public void setFavoritesNumber(int favoritesNumber) {
        this.favoritesNumber = favoritesNumber;
    }
}
