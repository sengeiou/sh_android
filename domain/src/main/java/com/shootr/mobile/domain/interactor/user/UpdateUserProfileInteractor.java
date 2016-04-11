package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.validation.CreateUserValidator;
import com.shootr.mobile.domain.validation.FieldValidationError;

import javax.inject.Inject;

public class UpdateUserProfileInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;

    private Interactor.CompletedCallback callback;
    private Interactor.ErrorCallback errorCallback;
    private User user;

    @Inject public UpdateUserProfileInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void updateProfile(User user, CompletedCallback callback, ErrorCallback errorCallback) {
        this.user = user;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        sendUpdatedProfileToServer();
    }

    private void sendUpdatedProfileToServer() {
        try {
            User updatedUserEntity = updateEntityWithValues(user);
            remoteUserRepository.updateUserProfile(updatedUserEntity);
            sessionRepository.setCurrentUser(localUserRepository.getUserById(sessionRepository.getCurrentUserId()));
            notifyLoaded();
        } catch (EmailAlreadyExistsException e) {
            handleServerError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE, CreateUserValidator.FIELD_EMAIL);
        } catch (UsernameAlreadyExistsException e) {
            handleServerError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE,
              CreateUserValidator.FIELD_USERNAME);
        } catch (ServerCommunicationException communicationError) {
            notifyError(communicationError);
        }
    }

    private User updateEntityWithValues(User updatedUser) {
        User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setBio(updatedUser.getBio());
        user.setWebsite(updatedUser.getWebsite());
        user.setEmail(updatedUser.getEmail());
        user.setEmailConfirmed(updatedUser.isEmailConfirmed());
        return user;
    }

    protected void handleServerError(String errorCode, int field) {
        FieldValidationError fieldValidationError = new FieldValidationError(errorCode, field);
        notifyError(new DomainValidationException(fieldValidationError));
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
