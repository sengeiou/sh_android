package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.ResetPasswordView;
import javax.inject.Inject;

public class ResetPasswordPresenter implements Presenter {

    private ResetPasswordView resetPasswordView;
    private Boolean errorResettingAccount;

    //TODO implementar el constructor
    @Inject public ResetPasswordPresenter(){

    }

    public void initialize(ResetPasswordView resetPasswordView){
        this.errorResettingAccount = false;
        setView(resetPasswordView);
    }

    public void setView(ResetPasswordView view) {
        this.resetPasswordView = view;
    }

    public void inputTextChanged() {
        errorResettingAccount = false;
        resetPasswordView.hideLogin();
        String usernameOrEmail= resetPasswordView.getUsernameOrEmail();
        if (isUsernameOrEmailValid(usernameOrEmail)) {
            resetPasswordView.enableResetButton();
        } else {
            resetPasswordView.disableResetButton();
        }
    }

    private boolean isUsernameOrEmailValid(String usernameOrEmail) {
        return usernameOrEmail.length() > 2;
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    public void attempReset() {
        //TODO implementar el callback
        if(errorResettingAccount.equals(false)){
            resetPasswordView.showLoading();
            if(resetPasswordView.getUsernameOrEmail().equals("username")){

            }else{
                errorResettingAccount = true;
                resetPasswordView.hideLogin();
                showErrorInView();
            }
        }
    }

    private void showErrorInView() {
        //TODO implementar el control de errores. No olvidar el TimeOut.
        resetPasswordView.showError();
    }
}
