package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;

public class WatchingStreamResultViewHolder extends StreamResultViewHolder {

    private final OnUnwatchClickListener unwatchClickListener;

    @Bind(R.id.stream_remove) ImageView remove;

    public WatchingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      ImageLoader imageLoader, OnUnwatchClickListener unwatchClickListener) {
        super(itemView, onStreamClickListener, imageLoader);
        this.unwatchClickListener = unwatchClickListener;
    }

    @Override
    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        super.render(streamResultModel, showSeparator);
        remove.setVisibility(View.VISIBLE);
        setUnwatchClickListener();
    }

    private void setUnwatchClickListener() {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                unwatchClickListener.onUnwatchClick();
            }
        });
    }
}
