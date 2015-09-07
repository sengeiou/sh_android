package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.WelcomePageView;

public class WelcomePagePresenter implements Presenter {

    private WelcomePageView welcomePageView;

    protected void setView(WelcomePageView welcomePageView) {
        this.welcomePageView = welcomePageView;
    }

    public void initialize(WelcomePageView welcomePageView) {
        setView(welcomePageView);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
