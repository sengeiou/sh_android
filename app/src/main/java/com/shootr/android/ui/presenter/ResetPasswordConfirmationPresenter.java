package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ConfirmResetPasswordInteractor;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.views.ResetPasswordConfirmationView;
import javax.inject.Inject;

public class ResetPasswordConfirmationPresenter implements Presenter {

    private final ConfirmResetPasswordInteractor confirmResetPasswordInteractor;

    private ResetPasswordConfirmationView resetPasswordConfirmationView;
    private ForgotPasswordUserModel forgotPasswordUserModel;

    @Inject
    public ResetPasswordConfirmationPresenter(ConfirmResetPasswordInteractor confirmResetPasswordInteractor) {
        this.confirmResetPasswordInteractor = confirmResetPasswordInteractor;
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
        confirmResetPasswordInteractor.confirmResetPassword(forgotPasswordUserModel.getIdUser(), //
          new Interactor.CompletedCallback() {
              @Override
              public void onCompleted() {
                  resetPasswordConfirmationView.showDoneButton();
              }
          }, //
          new Interactor.ErrorCallback() {
              @Override
              public void onError(ShootrException error) {
                  //TODO
              }
          });
    }

    public void done() {
        //TODO
    }

    private void showUserDataInView() {
        resetPasswordConfirmationView.showAvatar(forgotPasswordUserModel.getAvatarUrl());
        resetPasswordConfirmationView.showUsername(forgotPasswordUserModel.getUsername());
    }

    @Override
    public void resume() {
        /* no-op */
    }

    @Override
    public void pause() {
        /* no-op */
    }
}
