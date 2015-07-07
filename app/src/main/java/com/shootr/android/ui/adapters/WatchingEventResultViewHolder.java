package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;

public class WatchingEventResultViewHolder extends EventResultViewHolder {

    private final OnUnwatchClickListener unwatchClickListener;

    @Bind(R.id.event_remove) ImageView remove;

    public WatchingEventResultViewHolder(View itemView, OnEventClickListener onEventClickListener,
      PicassoWrapper picasso, OnUnwatchClickListener unwatchClickListener) {
        super(itemView, onEventClickListener, picasso);
        this.unwatchClickListener = unwatchClickListener;
    }

    public void render(EventResultModel event, boolean isCheckedIn) {
        super.render(event, isCheckedIn);
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
