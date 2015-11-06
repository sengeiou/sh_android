package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamResultModel;

public abstract class OnStreamClickListener {

    public abstract void onStreamClick(StreamResultModel stream);

    public boolean onStreamLongClick(StreamResultModel stream) {
        return false;
    }
}
