package com.shootr.android.ui.adapters.listeners;

import com.shootr.android.ui.model.ShotModel;

public abstract class OnShotClickListener {

    public abstract void onShotClick(ShotModel shot);

    public boolean onShotLongClick(ShotModel shotModel) {
        return false;
    }

}
