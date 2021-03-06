package com.shootr.mobile.domain.model.stream;

import java.util.Collections;
import java.util.List;

public class Listing {

    private List<StreamSearchResult> holdingStreams;
    private List<StreamSearchResult> favoritedStreams;
    private Boolean includeHolding;
    private Boolean includeFavorited;

    private Listing() {
        /* private constructor */
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<StreamSearchResult> getHoldingStreams() {
        return holdingStreams;
    }

    public List<StreamSearchResult> getFavoritedStreams() {
        return favoritedStreams;
    }

    public Boolean includesHolding() {
        return includeHolding;
    }

    public Boolean includesFavorited() {
        return includeFavorited;
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
            listing.includeHolding = true;
            return this;
        }

        public Builder favoritedStreams(List<StreamSearchResult> favorited) {
            listing.favoritedStreams = favorited;
            listing.includeFavorited = true;
            return this;
        }

        public Listing build() {
            return listing;
        }
    }
}
