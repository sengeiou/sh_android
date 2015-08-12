package com.shootr.android.ui.adapters.listeners;

import com.shootr.android.ui.model.StreamResultModel;

public interface OnFavoriteClickListener {

    void onFavoriteClick(StreamResultModel stream);

    void onRemoveFavoriteClick(StreamResultModel stream);
}
