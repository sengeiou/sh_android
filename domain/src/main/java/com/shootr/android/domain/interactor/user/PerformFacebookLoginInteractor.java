package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.InvalidLoginException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.user.LoginException;
import com.shootr.android.domain.service.user.ShootrUserService;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class PerformFacebookLoginInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private String facebookToken;
    private ErrorCallback errorCallback;
    private Callback<Boolean> callback;


    @Inject public PerformFacebookLoginInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void attempLogin(String facebookToken, Callback<Boolean> callback, ErrorCallback errorCallback) {
        this.facebookToken = checkNotNull(facebookToken);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            notifyLoaded(shootrUserService.performFacebookLogin(facebookToken));
        } catch (InvalidLoginException loginError){
            notifyError(new LoginException(loginError));
        } catch (ShootrException unknownException){
            notifyError(unknownException);
        }
    }

    private void notifyLoaded(final Boolean isNewUser) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(isNewUser);
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
