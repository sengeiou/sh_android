package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.ResetPasswordRequestView;

public class ResetPasswordRequestPresenter implements Presenter {

    private ResetPasswordRequestView resetPasswordRequestView;

    public void initialize(ResetPasswordRequestView resetPasswordRequestView) {
        this.resetPasswordRequestView = resetPasswordRequestView;
    }

    public void next() {
        //TODO
    }

    public void onUsernameOrEmailChanged(String usernameOrEmail) {
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
