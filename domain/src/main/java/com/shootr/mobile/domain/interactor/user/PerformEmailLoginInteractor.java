package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForShootrException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.user.LoginException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import javax.inject.Inject;

public class PerformEmailLoginInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private CompletedCallback completedCallback;

    private String usernameOrEmail;
    private String password;

    @Inject
    public PerformEmailLoginInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void attempLogin(String usernameOrEmail, String password, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.performLogin(usernameOrEmail, password);
            notifyLoaded();
        } catch (InvalidLoginException loginError) {
            handleServerError(new LoginException(loginError));
        } catch (InvalidLoginMethodForShootrException loginShootrError) {
            notifyError(loginShootrError);
        } catch (ShootrException unknownError) {
            handleServerError(unknownError);
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void handleServerError(ShootrException shootrException) {
        notifyError(shootrException);
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
