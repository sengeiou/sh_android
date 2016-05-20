package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.views.NewShotBarView;
import java.io.File;

public class NullNewShotBarView implements NewShotBarView {

    @Override public void openNewShotView() {
        /* no-op */
    }

    @Override public void pickImage() {
        /* no-op */
    }

    @Override public void showHolderOptions() {
        /* no-op */
    }

    @Override public void openNewShotViewWithImage(File image) {
        /* no-op */
    }

    @Override public void openEditTopicDialog() {
        /* no-op */
    }

    @Override public void showDraftsButton() {
        /* no-op */
    }

    @Override public void hideDraftsButton() {
        /* no-op */
    }

    @Override public void showError(String errorMessage) {
        /* no-op */
    }
}
