package com.shootr.mobile.ui.views;

import java.io.File;

public interface NewShotBarView {

    void openNewShotView();

    void pickImage();

    void pickTopic();

    void openNewShotViewWithImage(File image);

    void openEditTopicDialog();

    void showDraftsButton();

    void hideDraftsButton();

    void showError(String errorMessage);
}
