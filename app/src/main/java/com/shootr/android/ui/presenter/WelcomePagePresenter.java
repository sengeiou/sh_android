package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.WelcomePageView;
import javax.inject.Inject;

public class WelcomePagePresenter implements Presenter {

    private WelcomePageView welcomePageView;

    @Inject public WelcomePagePresenter() {
    }

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
