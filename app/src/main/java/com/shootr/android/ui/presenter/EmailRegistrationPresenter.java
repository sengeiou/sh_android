package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.CreateAccountInteractor;
import com.shootr.android.ui.views.EmailRegistrationView;
import javax.inject.Inject;

public class EmailRegistrationPresenter implements Presenter {

    private final CreateAccountInteractor createAccountInteractor;

    private EmailRegistrationView emailRegistrationView;

    @Inject public EmailRegistrationPresenter(CreateAccountInteractor createAccountInteractor) {
        this.createAccountInteractor = createAccountInteractor;
    }

    protected void setView(EmailRegistrationView emailRegistrationView) {
        this.emailRegistrationView = emailRegistrationView;
    }

    public void initialize(EmailRegistrationView emailRegistrationView) {
        this.setView(emailRegistrationView);
    }

    public void onCreateAccount() {
        showViewLoading();
        String email = emailRegistrationView.getEmail();
        String username = emailRegistrationView.getUsername();
        String password = emailRegistrationView.getPassword();

        createAccountInteractor.createAccount(email, username, password, //
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  //TODO
              }
          }, //
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  //TODO
              }
          });
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
