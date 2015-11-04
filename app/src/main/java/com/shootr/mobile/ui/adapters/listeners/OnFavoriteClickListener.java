package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamResultModel;

public interface OnFavoriteClickListener {

    void onFavoriteClick(StreamResultModel stream);

    void onRemoveFavoriteClick(StreamResultModel stream);
}
