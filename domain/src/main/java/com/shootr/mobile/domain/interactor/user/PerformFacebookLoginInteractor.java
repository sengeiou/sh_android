package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForFacebookException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.user.LoginException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PerformFacebookLoginInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private String facebookToken;
    private ErrorCallback errorCallback;
    private Callback<Boolean> callback;
    private LocaleProvider localeProvider;

    @Inject
    public PerformFacebookLoginInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
        this.localeProvider = localeProvider;
    }

    public void attempLogin(String facebookToken, Callback<Boolean> callback, ErrorCallback errorCallback) {
        this.facebookToken = checkNotNull(facebookToken);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            notifyLoaded(
                shootrUserService.performFacebookLogin(facebookToken, localeProvider.getLocale()));
        } catch (InvalidLoginException loginError) {
            notifyError(new LoginException(loginError));
        } catch (MassiveRegisterErrorException massiveRegisterErrorException) {
            notifyError(massiveRegisterErrorException);
        } catch (InvalidLoginMethodForFacebookException loginFacebookError) {
            notifyError(loginFacebookError);
        } catch (ShootrException unknownException) {
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
