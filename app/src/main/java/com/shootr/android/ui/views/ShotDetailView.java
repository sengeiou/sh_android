package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;

public interface ShotDetailView  {

    void renderShot(ShotModel shotModel);

    void openImage(String imageUrl);
}
