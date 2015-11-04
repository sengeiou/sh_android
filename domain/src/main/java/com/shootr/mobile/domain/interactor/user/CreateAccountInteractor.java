package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.validation.CreateUserValidator;
import java.util.List;
import javax.inject.Inject;

public class CreateAccountInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;

    private String email;
    private String username;
    private String password;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public CreateAccountInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrUserService = shootrUserService;
    }

    public void createAccount(String email, String username, String password, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.callback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        if (validateInput()) {
            try {
                shootrUserService.createAccount(username, email, password);
                notifyLoaded();
            } catch (com.shootr.mobile.domain.exception.EmailAlreadyExistsException e) {
                handleServerError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE, CreateUserValidator.FIELD_EMAIL);
            } catch (com.shootr.mobile.domain.exception.UsernameAlreadyExistsException e) {
                handleServerError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE,
                  CreateUserValidator.FIELD_USERNAME);
            } catch (com.shootr.mobile.domain.exception.ServerCommunicationException communicationError) {
                notifyError(communicationError);
            }
        }
    }

    protected void handleServerError(String errorCode, int field) {
        com.shootr.mobile.domain.validation.FieldValidationError fieldValidationError =
          new com.shootr.mobile.domain.validation.FieldValidationError(errorCode, field);
        notifyError(new com.shootr.mobile.domain.exception.DomainValidationException(fieldValidationError));
    }

    private boolean validateInput() {
        List<com.shootr.mobile.domain.validation.FieldValidationError> validationErrors = new CreateUserValidator().validate(email, username, password);
        if (validationErrors.isEmpty()) {
            return true;
        } else {
            notifyError(new com.shootr.mobile.domain.exception.DomainValidationException(validationErrors));
            return false;
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
