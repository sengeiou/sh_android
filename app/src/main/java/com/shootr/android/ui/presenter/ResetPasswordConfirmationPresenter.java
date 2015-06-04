package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.views.ResetPasswordConfirmationView;

public class ResetPasswordConfirmationPresenter implements Presenter {

    private ResetPasswordConfirmationView resetPasswordConfirmationView;
    private ForgotPasswordUserModel forgotPasswordUserModel;

    public void initialize(ResetPasswordConfirmationView resetPasswordConfirmationView,
      ForgotPasswordUserModel forgotPasswordUserModel) {
        this.resetPasswordConfirmationView = resetPasswordConfirmationView;
        this.forgotPasswordUserModel = forgotPasswordUserModel;
        this.showUserDataInView();
    }

    public void confirm() {
        //TODO
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
