package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.SupportView;

public class SupportPresenter implements Presenter {

    private SupportView supportView;

    private void setView(SupportView supportView) {
        this.supportView = supportView;
    }

    private void initialize(SupportView supportView) {
        this.setView(supportView);
        //TODO call interactors for getting blog and help streams
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
