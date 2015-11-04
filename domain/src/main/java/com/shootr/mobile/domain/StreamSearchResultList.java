package com.shootr.mobile.domain;

import java.util.List;

public class StreamSearchResultList {

    private List<StreamSearchResult> streamSearchResults;
    private StreamSearchResult currentWatchingStream;

    public StreamSearchResultList(List<StreamSearchResult> streamSearchResults) {
        this.streamSearchResults = streamSearchResults;
    }

    public StreamSearchResultList(List<StreamSearchResult> streamSearchResults, StreamSearchResult currentWatchingStream) {
        this.streamSearchResults = streamSearchResults;
        this.currentWatchingStream = currentWatchingStream;
    }

    public List<StreamSearchResult> getStreamSearchResults() {
        return streamSearchResults;
    }

    public void setStreamSearchResults(List<StreamSearchResult> streamSearchResults) {
        this.streamSearchResults = streamSearchResults;
    }

    public StreamSearchResult getCurrentWatchingStream() {
        return currentWatchingStream;
    }

    public void setCurrentWatchingStream(StreamSearchResult currentWatchingStream) {
        this.currentWatchingStream = currentWatchingStream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamSearchResultList)) return false;

        StreamSearchResultList that = (StreamSearchResultList) o;

        if (!streamSearchResults.equals(that.streamSearchResults)) return false;
        if (currentWatchingStream != null ? !currentWatchingStream.equals(that.currentWatchingStream)
          : that.currentWatchingStream != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = streamSearchResults.hashCode();
        result = 31 * result + (currentWatchingStream != null ? currentWatchingStream.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamSearchResultList{" +
          "streamSearchResults=" + streamSearchResults +
          ", currentWatchingStream=" + currentWatchingStream +
          '}';
    }
}
