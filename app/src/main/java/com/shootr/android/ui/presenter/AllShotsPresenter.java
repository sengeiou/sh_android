package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.AllShotsView;
import javax.inject.Inject;

public class AllShotsPresenter implements Presenter {

    private AllShotsView allShotsView;
    private String userId;

    @Inject public AllShotsPresenter() {
    }

    public void initialize(AllShotsView allShotsView, String userId) {
        setView(allShotsView);
        this.userId = userId;
        //TODO call the interactor here
    }

    private void setView(AllShotsView allShotsView) {
        this.allShotsView = allShotsView;
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
