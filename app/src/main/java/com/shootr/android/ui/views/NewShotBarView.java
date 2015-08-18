package com.shootr.android.ui.views;

import java.io.File;

public interface NewShotBarView {

    void openNewShotView();

    void pickImage();

    void openNewShotViewWithImage(File image);

    void showDraftsButton();

    void hideDraftsButton();

    void showStreamIsRemoved();

    void openDrafts();
}
