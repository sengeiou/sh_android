package com.shootr.android.ui.presenter;

import android.text.TextUtils;
import com.shootr.android.ui.views.EmailLoginView;
import javax.inject.Inject;

public class EmailLoginPresenter implements Presenter {

    private EmailLoginView emailLoginView;

    @Inject public EmailLoginPresenter() {
    }

    protected void setView(EmailLoginView emailLoginView){
        this.emailLoginView = emailLoginView;
    }

    public void initialize(EmailLoginView emailLoginView){
        this.setView(emailLoginView);
    }

    public void attempLogin(){
        emailLoginView.goToTimeline();
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    public void inputTextChanged() {
        String usernameOrEmail= emailLoginView.getUsernameOrEmail();
        String password = emailLoginView.getPassword();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            emailLoginView.emailButtonIsDisabled();
            emailLoginView.emailButtonLoginStateHasChanged();
        } else {
            emailLoginView.emailButtonIsEnabled();
            emailLoginView.emailButtonLoginStateHasChanged();
        }

    }
}
