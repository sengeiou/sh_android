package com.shootr.android.domain;

import java.util.Collections;
import java.util.List;

public class Listing {

    private List<StreamSearchResult> holdingStreams;
    private List<Stream> favoritedStreams;
    private Boolean includeHolding;
    private Boolean includeFavorited;

    private Listing() {
        /* private constructor */
    }

    public List<StreamSearchResult> getHoldingStreams() {
        return holdingStreams;
    }

    public List<Stream> getFavoritedStreams() {
        return favoritedStreams;
    }

    public Boolean getIncludeHolding() {
        return includeHolding;
    }

    public Boolean getIncludeFavorited() {
        return includeFavorited;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Listing listing = new Listing();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            listing.holdingStreams = Collections.emptyList();
            listing.favoritedStreams = Collections.emptyList();
            listing.includeHolding = false;
            listing.includeFavorited = false;
        }

        public Builder holdingStreams(List<StreamSearchResult> holding) {
            listing.holdingStreams = holding;
            listing.includeHolding = holding.size() > 0;
            return this;
        }

        public Builder favoritedStreams(List<Stream> favorited) {
            listing.favoritedStreams = favorited;
            listing.includeFavorited = favorited.size() > 0;
            return this;
        }

        public Listing build() {
            return listing;
        }
    }
}
