package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.EmailInUseException;
import com.shootr.mobile.domain.service.EmailIsAuthenticatedException;
import com.shootr.mobile.domain.service.InsufficientAuthenticationException;
import javax.inject.Inject;

public class ChangeEmailInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;

    private String email;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public ChangeEmailInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void changeEmail(String email, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.email = email;
        this.callback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.changeEmail(email);
            notifyLoaded();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        } catch (EmailAlreadyExistsException error) {
            notifyError(new EmailInUseException(error));
        } catch (UnauthorizedRequestException error) {
            notifyError(new InsufficientAuthenticationException(error));
        } catch (EmailAlreadyConfirmedException error) {
            notifyError(new EmailIsAuthenticatedException(error));
        } catch (NullPointerException unknownError) {
            notifyError(new InsufficientAuthenticationException(unknownError));
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
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
