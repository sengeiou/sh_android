package com.shootr.mobile.domain;

public class StreamSearchResult {

    private Stream stream;
    private int watchersNumber;
    private boolean isWatching;
    private int favoritesNumber;

    public StreamSearchResult() {
    }

    public StreamSearchResult(Stream stream, int watchersNumber) {
        this.stream = stream;
        this.watchersNumber = watchersNumber;
    }

    public StreamSearchResult(Stream stream, Integer watchersNumber) {
        this.stream = stream;
        this.watchersNumber = watchersNumber != null ? watchersNumber : 0;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public int getWatchersNumber() {
        return watchersNumber;
    }

    public void setWatchersNumber(int watchersNumber) {
        this.watchersNumber = watchersNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamSearchResult)) return false;

        StreamSearchResult that = (StreamSearchResult) o;

        if (watchersNumber != that.watchersNumber) return false;
        return stream.equals(that.stream);
    }

    @Override public int hashCode() {
        int result = stream.hashCode();
        result = 31 * result + watchersNumber;
        return result;
    }

    @Override public String toString() {
        return "StreamSearchResult{" +
          "stream=" + stream +
          ", watchersNumber=" + watchersNumber +
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
