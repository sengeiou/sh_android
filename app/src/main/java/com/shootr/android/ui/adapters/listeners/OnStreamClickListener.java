package com.shootr.android.ui.adapters.listeners;

import com.shootr.android.ui.model.StreamResultModel;

public abstract class OnStreamClickListener {

    public abstract void onStreamClick(StreamResultModel stream);

    public boolean onStreamLongClick(StreamResultModel stream) {
        return false;
    }
}
