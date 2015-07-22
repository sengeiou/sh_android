package com.shootr.android.domain;

public class EventSearchResult {

    private Stream stream;
    private int watchersNumber;

    public EventSearchResult() {
    }

    public EventSearchResult(Stream stream, int watchersNumber) {
        this.stream = stream;
        this.watchersNumber = watchersNumber;
    }

    public EventSearchResult(Stream stream, Integer watchersNumber) {
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
        if (!(o instanceof EventSearchResult)) return false;

        EventSearchResult that = (EventSearchResult) o;

        if (watchersNumber != that.watchersNumber) return false;
        return stream.equals(that.stream);
    }

    @Override public int hashCode() {
        int result = stream.hashCode();
        result = 31 * result + watchersNumber;
        return result;
    }

    @Override public String toString() {
        return "EventSearchResult{" +
          "event=" + stream +
          ", watchersNumber=" + watchersNumber +
          '}';
    }
}
