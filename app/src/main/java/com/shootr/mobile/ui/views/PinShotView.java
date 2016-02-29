package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;

public interface PinShotView {

    void notifyPinnedShot(ShotModel shotModel);

    void showPinned();
}
