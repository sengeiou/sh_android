package com.shootr.mobile.domain.model.stream;

public class StreamSearchResult {

    private Stream stream;
    private boolean isWatching;
    private int favoritesNumber;

    public StreamSearchResult() {
    }

    public StreamSearchResult(Stream stream) {
        this.stream = stream;
    }

    public StreamSearchResult(Stream stream, Integer followingWatchersNumber) {
        this.stream = stream;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamSearchResult)) return false;

        StreamSearchResult that = (StreamSearchResult) o;

        return stream.equals(that.stream);
    }

    @Override public int hashCode() {
        int result = stream.hashCode();
        return result;
    }

    @Override public String toString() {
        return "StreamSearchResult{" +
          "stream=" + stream +
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
