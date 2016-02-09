package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidPasswordException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.ChangePasswordInvalidException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class ChangePasswordInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private ErrorCallback errorCallback;
    private CompletedCallback completedCallback;
    private String currentPassword;
    private String newPassword;
    private LocaleProvider localeProvider;

    @Inject public ChangePasswordInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
        this.localeProvider = localeProvider;
    }

    public void attempToChangePassword(String currentPassword, String newPassword, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            shootrUserService.changePassword(currentPassword, newPassword, localeProvider.getLocale());
            notifyLoaded();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        } catch (InvalidPasswordException error) {
            notifyError(new ChangePasswordInvalidException(error));
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
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
