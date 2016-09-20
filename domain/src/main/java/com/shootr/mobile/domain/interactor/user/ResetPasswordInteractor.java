package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.ForgotPasswordResult;
import com.shootr.mobile.domain.service.ResetPasswordException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import javax.inject.Inject;

public class ResetPasswordInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private Callback<ForgotPasswordResult> forgotPasswordResultCallback;

    private String usernameOrEmail;

    @Inject public ResetPasswordInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void attempResetPassword(String usernameOrEmail, Callback<ForgotPasswordResult> forgotPasswordResultCallback,
      ErrorCallback errorCallback) {
        this.usernameOrEmail = usernameOrEmail;
        this.forgotPasswordResultCallback = forgotPasswordResultCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            ForgotPasswordResult forgotPasswordResult = shootrUserService.performResetPassword(usernameOrEmail);
            notifyLoaded(forgotPasswordResult);
        } catch (InvalidForgotPasswordException forgotError) {
            notifyError(new ResetPasswordException(forgotError));
        } catch (ShootrException unknownError) {
            notifyError(unknownError);
        }
    }

    private void notifyError(final ShootrException exception) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(exception);
            }
        });
    }

    private void notifyLoaded(final ForgotPasswordResult forgotPasswordResult) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                forgotPasswordResultCallback.onLoaded(forgotPasswordResult);
            }
        });
    }
}
