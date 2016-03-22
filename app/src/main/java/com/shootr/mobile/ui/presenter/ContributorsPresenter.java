package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.ui.views.ContributorsView;
import javax.inject.Inject;

public class ContributorsPresenter implements Presenter {

    private ContributorsView view;
    private String idStream;

    @Inject public ContributorsPresenter(ContributorsView contributorsView) {
        this.view = contributorsView;
    }

    protected void setView(ContributorsView contributorsView) {
        this.view = contributorsView;
    }

    protected void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public void initialize(ContributorsView contributorsView, String idStream) {
        setView(contributorsView);
        setIdStream(idStream);
        loadContributors();
    }

    private void loadContributors() {
        view.hideEmpty();
        view.showLoading();
        //TODO interactor
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
