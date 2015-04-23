package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.PerformEmailLoginInteractor;
import com.shootr.android.ui.views.EmailLoginView;
import javax.inject.Inject;

public class EmailLoginPresenter implements Presenter {

    private EmailLoginView emailLoginView;

    private final PerformEmailLoginInteractor emailLoginInteractor;

    @Inject public EmailLoginPresenter(PerformEmailLoginInteractor emailLoginInteractor) {
        this.emailLoginInteractor = emailLoginInteractor;
    }

    protected void setView(EmailLoginView emailLoginView){
        this.emailLoginView = emailLoginView;
    }

    public void initialize(EmailLoginView emailLoginView){
        this.setView(emailLoginView);
    }

    public void attempLogin(){
        emailLoginView.setLoginButtonLoading(true);
        emailLoginInteractor.attempLogin(emailLoginView.getUsernameOrEmail(),
          emailLoginView.getPassword(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  emailLoginView.goToTimeline();
              }
          },
          new Interactor.ErrorCallback() {

              @Override public void onError(ShootrException error) {
                  stopLoadingLoginButton();
                  showErrorsInEmailLoginButton();
              }
          });
    }

    private void stopLoadingLoginButton() {
        emailLoginView.emailButtonIsDisabled();
        emailLoginView.emailButtonShowsError();
    }

    private void showErrorsInEmailLoginButton() {
        emailLoginView.emailButtonPrintsError();
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
