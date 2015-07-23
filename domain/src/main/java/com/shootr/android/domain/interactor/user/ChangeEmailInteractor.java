package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.EmailAlreadyConfirmed;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.UnauthorizedRequestException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.EmailInUseException;
import com.shootr.android.domain.service.EmailIsAuthenticatedException;
import com.shootr.android.domain.service.InsufficientAuthenticationException;
import com.shootr.android.domain.service.user.ShootrUserService;
import javax.inject.Inject;

public class ChangeEmailInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private String email;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public ChangeEmailInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
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
        } catch (EmailAlreadyConfirmed error) {
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
