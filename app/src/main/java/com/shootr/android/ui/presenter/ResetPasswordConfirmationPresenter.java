package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.ResetPasswordConfirmationView;

public class ResetPasswordConfirmationPresenter implements Presenter {

    private ResetPasswordConfirmationView resetPasswordConfirmationView;

    public void initialize(ResetPasswordConfirmationView resetPasswordConfirmationView) {
        this.resetPasswordConfirmationView = resetPasswordConfirmationView;
    }

    public void confirm() {
        //TODO
    }

    public void done() {
        //TODO
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
