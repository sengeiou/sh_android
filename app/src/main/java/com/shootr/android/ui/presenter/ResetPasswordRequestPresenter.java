package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.ResetPasswordRequestView;

public class ResetPasswordRequestPresenter implements Presenter {

    private ResetPasswordRequestView resetPasswordRequestView;

    protected void setView(ResetPasswordRequestView resetPasswordRequestView) {
        this.resetPasswordRequestView = resetPasswordRequestView;
    }

    public void initialize(ResetPasswordRequestView resetPasswordRequestView) {
        setView(resetPasswordRequestView);
        resetPasswordRequestView.disableNextButton();
    }

    public void next() {
        //TODO
    }

    public void onUsernameOrEmailChanged(String usernameOrEmail) {
        if (!usernameOrEmail.isEmpty()) {
            resetPasswordRequestView.enableNextButton();
        }
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
