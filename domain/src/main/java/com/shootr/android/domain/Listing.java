package com.shootr.android.domain;

import java.util.List;

public class Listing {

    private List<StreamSearchResult> holdingStreams;
    private List<Stream> favoritedStreams;
    private Boolean includeHolding;
    private Boolean includeFavorited;

    public List<StreamSearchResult> getHoldingStreams() {
        return holdingStreams;
    }

    public void setHoldingStreams(List<StreamSearchResult> holdingStreams) {
        this.holdingStreams = holdingStreams;
    }

    public List<Stream> getFavoritedStreams() {
        return favoritedStreams;
    }

    public void setFavoritedStreams(List<Stream> favoritedStreams) {
        this.favoritedStreams = favoritedStreams;
    }

    public Boolean getIncludeHolding() {
        return includeHolding;
    }

    public void setIncludeHolding(Boolean includeHolding) {
        this.includeHolding = includeHolding;
    }

    public Boolean getIncludeFavorited() {
        return includeFavorited;
    }

    public void setIncludeFavorited(Boolean includeFavorited) {
        this.includeFavorited = includeFavorited;
    }
}
