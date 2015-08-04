package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class ListingStreamResultViewHolder extends StreamResultViewHolder {

    private final OnFavoriteClickListener onFavoriteClickListener;

    @Bind(R.id.favorite_stream_added) ImageView favoriteAdded;
    @Bind(R.id.favorite_stream_not_added) ImageView favoriteNotAdded;

    public ListingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      PicassoWrapper picasso, OnFavoriteClickListener onFavoriteClickListener) {
        super(itemView, onStreamClickListener, picasso);
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    public void render(StreamResultModel streamResultModel, Integer position) {
        super.render(streamResultModel, position);
        setUnwatchClickListener();
    }

    private void setUnwatchClickListener() {
        favoriteAdded.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onFavoriteClickListener.onFavoriteClik();
            }
        });
        favoriteNotAdded.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onFavoriteClickListener.onFavoriteClik();
            }
        });
    }

}
