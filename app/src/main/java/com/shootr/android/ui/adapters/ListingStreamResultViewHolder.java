package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnRemoveFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class ListingStreamResultViewHolder extends StreamResultViewHolder {

    private final OnFavoriteClickListener onFavoriteClickListener;
    private final OnRemoveFavoriteClickListener onRemoveFavoriteClickListener;

    @Bind(R.id.favorite_stream_added) ImageView favoriteAdded;
    @Bind(R.id.favorite_stream_not_added) ImageView favoriteNotAdded;

    public ListingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      PicassoWrapper picasso, OnFavoriteClickListener onFavoriteClickListener,
      OnRemoveFavoriteClickListener onRemoveFavoriteClickListener) {
        super(itemView, onStreamClickListener, picasso);
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onRemoveFavoriteClickListener = onRemoveFavoriteClickListener;
    }

    public void render(StreamResultModel streamResultModel, Integer position, Boolean favorite) {
        super.render(streamResultModel);
        setFavoriteClickListener(streamResultModel);
        if(favorite) {
            favoriteAdded.setVisibility(View.VISIBLE);
            favoriteNotAdded.setVisibility(View.GONE);
        } else {
            favoriteNotAdded.setVisibility(View.VISIBLE);
            favoriteAdded.setVisibility(View.GONE);
        }
    }

    private void setFavoriteClickListener(final StreamResultModel streamResult) {
        favoriteAdded.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onRemoveFavoriteClickListener.onRemoveFavoriteClick(streamResult);
                favoriteAdded.setVisibility(View.GONE);
                favoriteNotAdded.setVisibility(View.VISIBLE);
            }
        });
        favoriteNotAdded.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onFavoriteClickListener.onFavoriteClick(streamResult);
                favoriteAdded.setVisibility(View.VISIBLE);
                favoriteNotAdded.setVisibility(View.GONE);
            }
        });
    }

}
