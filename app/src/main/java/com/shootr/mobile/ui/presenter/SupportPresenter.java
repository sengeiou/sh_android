package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.ui.views.SupportView;
import javax.inject.Inject;

public class SupportPresenter implements Presenter {

    private static final String EN_LOCALE = "en";

    private SupportView supportView;

     @Inject public SupportPresenter() {
    }

    protected void setView(SupportView supportView) {
        this.supportView = supportView;
    }

    public void initialize(SupportView supportView) {
        this.setView(supportView);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
