package com.shootr.android.ui.presenter;

import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.exception.InvalidResetPasswordException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ResetPasswordInteractor;
import com.shootr.android.ui.views.ResetPasswordView;
import com.shootr.android.util.ErrorMessageFactory;
import javax.inject.Inject;
import timber.log.Timber;

public class ResetPasswordPresenter implements Presenter {

    private ResetPasswordView resetPasswordView;

    private final ResetPasswordInteractor resetPasswordInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    @Inject public ResetPasswordPresenter(ResetPasswordInteractor resetPasswordInteractor,
      ErrorMessageFactory errorMessageFactory){
        this.resetPasswordInteractor = resetPasswordInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(ResetPasswordView view) {
        this.resetPasswordView = view;
    }

    public void initialize(ResetPasswordView resetPasswordView){
        this.setView(resetPasswordView);
    }

    public void inputTextChanged() {
        resetPasswordView.resetButtonToNormalStatus();
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

    public void attempReset() {
        resetPasswordView.showLoading();
        resetPasswordInteractor.attempResetPassword(resetPasswordView.getUsernameOrEmail(), new Interactor.Callback<ForgotPasswordResult>() {
            @Override public void onLoaded(ForgotPasswordResult forgotPasswordResult) {
                resetPasswordView.resetButtonToNormalStatus();
                //TODO aquí hay que llamar a la vista para que haga un intent a la activity de confirm
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                resetPasswordView.resetButtonToNormalStatus();
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        //TODO implementar el control de errores. No olvidar el TimeOut.
        String errorMessage;
        if(error instanceof InvalidResetPasswordException){
            errorMessage = errorMessageFactory.getLoginCredentialsError();
        }else if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        }else{
            Timber.e(error, "Unhandled error logging in");
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        resetPasswordView.showError(errorMessage);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
