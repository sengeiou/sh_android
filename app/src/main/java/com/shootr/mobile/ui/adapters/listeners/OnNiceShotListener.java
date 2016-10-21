package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.ShotModel;

public interface OnNiceShotListener {

    void markNice(ShotModel shot);

    void unmarkNice(String idShot);
}
