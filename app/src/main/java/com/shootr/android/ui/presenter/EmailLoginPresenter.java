package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.PerformEmailLoginInteractor;
import com.shootr.android.domain.service.user.LoginException;
import com.shootr.android.ui.views.EmailLoginView;
import com.shootr.android.util.ErrorMessageFactory;
import javax.inject.Inject;
import timber.log.Timber;

public class EmailLoginPresenter implements Presenter {

    private EmailLoginView emailLoginView;

    private final PerformEmailLoginInteractor emailLoginInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private boolean isInitialized = false;

    @Inject public EmailLoginPresenter(PerformEmailLoginInteractor emailLoginInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.emailLoginInteractor = emailLoginInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(EmailLoginView emailLoginView){
        this.emailLoginView = emailLoginView;
    }

    public void initialize(EmailLoginView emailLoginView){
        this.setView(emailLoginView);
        this.isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    //region Interaction methods
    public void attempLogin() {
        emailLoginView.showLoading();
        emailLoginInteractor.attempLogin(emailLoginView.getUsernameOrEmail(),
          emailLoginView.getPassword(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  emailLoginView.goToTimeline();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  emailLoginView.hideLoading();
                  showErrorInView(error);
              }
          });
    }

    public void inputTextChanged() {
        String usernameOrEmail= emailLoginView.getUsernameOrEmail();
        String password = emailLoginView.getPassword();
        boolean hasAnEmptyInputField = usernameOrEmail.isEmpty() || password.isEmpty();

        if (hasAnEmptyInputField) {
            emailLoginView.disableLoginButton();
        } else {
            emailLoginView.enableLoginButton();
        }
    }
    //endregion

    private void showErrorInView(ShootrException error) {
        emailLoginView.showError(errorMessageFactory.getMessageForError(error));
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

}
