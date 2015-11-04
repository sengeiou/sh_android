package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import javax.inject.Inject;

public class ResetPasswordInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private Callback<com.shootr.mobile.domain.ForgotPasswordResult> forgotPasswordResultCallback;

    private String usernameOrEmail;

    @Inject public ResetPasswordInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void attempResetPassword(String usernameOrEmail, Callback<com.shootr.mobile.domain.ForgotPasswordResult> forgotPasswordResultCallback, ErrorCallback errorCallback){
        this.usernameOrEmail = usernameOrEmail;
        this.forgotPasswordResultCallback = forgotPasswordResultCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception{
        try{
            com.shootr.mobile.domain.ForgotPasswordResult forgotPasswordResult = shootrUserService.performResetPassword(usernameOrEmail);
            notifyLoaded(forgotPasswordResult);
        } catch (com.shootr.mobile.domain.exception.InvalidForgotPasswordException forgotError) {
            notifyError(new com.shootr.mobile.domain.service.ResetPasswordException(forgotError));
        } catch (com.shootr.mobile.domain.exception.ShootrException unknownError){
            notifyError(unknownError);
        }
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException exception) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(exception);
            }
        });
    }

    private void notifyLoaded(final com.shootr.mobile.domain.ForgotPasswordResult forgotPasswordResult) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                forgotPasswordResultCallback.onLoaded(forgotPasswordResult);
            }
        });
    }
}
