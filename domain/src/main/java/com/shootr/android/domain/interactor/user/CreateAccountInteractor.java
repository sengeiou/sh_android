package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.user.ShootrUserService;
import com.shootr.android.domain.validation.CreateUserValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import java.util.List;
import javax.inject.Inject;

public class CreateAccountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrUserService shootrUserService;

    private String email;
    private String username;
    private String password;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public CreateAccountInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrUserService shootrUserService) {
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
            } catch (ShootrException shootrException) {
                handleServerError(shootrException);
            }
        }
    }

    private boolean validateInput() {
        List<FieldValidationError> validationErrors = new CreateUserValidator().validate(email, username, password);
        if (validationErrors.isEmpty()) {
            return true;
        } else {
            notifyError(new DomainValidationException(validationErrors));
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

    private void handleServerError(ShootrException e) {
        if (e.getCause() instanceof ShootrServerException) {
            ShootrServerException serverException = (ShootrServerException) e.getCause();
            String errorCode = serverException.getShootrError().getErrorCode();
            FieldValidationError validationError = validationErrorFromCode(errorCode);
            if (validationError != null) {
                notifyError(new DomainValidationException(validationError));
                return;
            }
        }
        notifyError(e);
    }

    private FieldValidationError validationErrorFromCode(String errorCode) {
        int field = fieldFromErrorCode(errorCode);
        if (field != 0) {
            return new FieldValidationError(errorCode, field);
        } else {
            return null;
        }
    }

    private int fieldFromErrorCode(String errorCode) {
        switch (errorCode) {
            case ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE:
            case ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT:
            case ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL:
                return CreateUserValidator.FIELD_EMAIL;
            case ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE:
            case ShootrError.ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS:
            case ShootrError.ERROR_CODE_REGISTRATION_USERNAME_NULL:
            case ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG:
            case ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT:
                return CreateUserValidator.FIELD_USERNAME;
            case ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME:
            case ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS:
            case ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL:
            case ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG:
            case ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT:
                return CreateUserValidator.FIELD_PASSWORD;
        }
        return 0;
    }
}
