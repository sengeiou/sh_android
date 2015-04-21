package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailRegistrationView;
import javax.inject.Inject;

public class EmailRegistrationPresenter implements Presenter {

    private EmailRegistrationView emailRegistrationView;

    @Inject public EmailRegistrationPresenter() {
    }

    protected void setView(EmailRegistrationView emailRegistrationView) {
        this.emailRegistrationView = emailRegistrationView;
    }

    public void initialize(EmailRegistrationView emailRegistrationView) {
        this.setView(emailRegistrationView);
    }

    public void onCreateAccount() {
        showViewLoading();
        //TODO interactor...
    }

    private void showViewLoading() {
        emailRegistrationView.showLoading();
        emailRegistrationView.hideCreateButton();
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
