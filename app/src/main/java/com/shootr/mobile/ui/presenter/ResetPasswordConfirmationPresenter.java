package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.SendPasswordResetEmailInteractor;
import com.shootr.mobile.ui.model.ForgotPasswordUserModel;
import com.shootr.mobile.ui.views.ResetPasswordConfirmationView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class ResetPasswordConfirmationPresenter implements Presenter {

    private final SendPasswordResetEmailInteractor sendPasswordResetEmailInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private ResetPasswordConfirmationView resetPasswordConfirmationView;
    private ForgotPasswordUserModel forgotPasswordUserModel;

    @Inject public ResetPasswordConfirmationPresenter(SendPasswordResetEmailInteractor sendPasswordResetEmailInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.sendPasswordResetEmailInteractor = sendPasswordResetEmailInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(ResetPasswordConfirmationView resetPasswordConfirmationView) {
        this.resetPasswordConfirmationView = resetPasswordConfirmationView;
    }

    protected void setUserModel(ForgotPasswordUserModel forgotPasswordUserModel) {
        this.forgotPasswordUserModel = forgotPasswordUserModel;
    }

    public void initialize(ResetPasswordConfirmationView resetPasswordConfirmationView,
      ForgotPasswordUserModel forgotPasswordUserModel) {
        this.setView(resetPasswordConfirmationView);
        this.setUserModel(forgotPasswordUserModel);
        this.showUserDataInView();
    }

    public void confirm() {
        if (resetPasswordConfirmationView != null) {
            resetPasswordConfirmationView.showLoading();
            resetPasswordConfirmationView.hideConfirmationButton();
            sendPasswordResetEmailInteractor.sendPasswordResetEmail(forgotPasswordUserModel.getIdUser(), //
                new Interactor.CompletedCallback() {
                    @Override public void onCompleted() {
                        resetPasswordConfirmationView.showDoneButton();
                        resetPasswordConfirmationView.
                            showPostConfirmationMessage(forgotPasswordUserModel.getEncryptedEmail());
                        resetPasswordConfirmationView.hideLoading();
                    }
                }, //
                new Interactor.ErrorCallback() {
                    @Override public void onError(ShootrException error) {
                        resetPasswordConfirmationView.hideLoading();
                        resetPasswordConfirmationView.showConfirmationButton();
                        showErrorInView(error);
                    }
                });
        }
    }

    public void done() {
        resetPasswordConfirmationView.navigateToLogin();
    }

    private void showUserDataInView() {
        resetPasswordConfirmationView.showAvatar(forgotPasswordUserModel.getAvatarUrl());
        resetPasswordConfirmationView.showUsername(forgotPasswordUserModel.getUsername());
    }

    private void showErrorInView(ShootrException error) {
        String errorMessage = errorMessageFactory.getMessageForError(error);
        resetPasswordConfirmationView.showError(errorMessage);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
