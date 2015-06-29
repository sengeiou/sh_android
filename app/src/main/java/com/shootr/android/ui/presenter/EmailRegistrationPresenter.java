package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.CreateAccountInteractor;
import com.shootr.android.domain.validation.CreateUserValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.views.EmailRegistrationView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class EmailRegistrationPresenter implements Presenter {

    private final CreateAccountInteractor createAccountInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private EmailRegistrationView emailRegistrationView;

    @Inject public EmailRegistrationPresenter(CreateAccountInteractor createAccountInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.createAccountInteractor = createAccountInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(EmailRegistrationView emailRegistrationView) {
        this.emailRegistrationView = emailRegistrationView;
    }

    public void initialize(EmailRegistrationView emailRegistrationView) {
        this.setView(emailRegistrationView);
    }

    //region Interaction methods
    public void usernameFocusRemoved() {
        if(emailRegistrationView.getUsernameInput() != null) {
            validateFieldOrShowError(CreateUserValidator.FIELD_USERNAME);
        }
    }

    public void emailFocusRemoved() {
        if(emailRegistrationView.getEmailInput() != null) {
            validateFieldOrShowError(CreateUserValidator.FIELD_EMAIL);
        }
    }

    public void passwordFocusRemoved() {
        if(emailRegistrationView.getPasswordInput() != null) {
            validateFieldOrShowError(CreateUserValidator.FIELD_PASSWORD);
        }
    }

    public void createAccount() {
        if (validateAllFieldsBeforeCreationAttemptOrShowErrors()) {
            emailRegistrationView.askEmailConfirmation();
        }
    }

    public void confirmAccountCreation() {
        showViewLoading();
        String email = emailRegistrationView.getEmailInput();
        String username = emailRegistrationView.getUsernameInput();
        String password = emailRegistrationView.getPasswordInput();

        createAccountInteractor.createAccount(email, username, password, //
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  emailRegistrationView.navigateToMainScreen();
              }
          }, //
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  hideViewLoading();
                  accountCreationError(error);
              }
          });
    }
    //endregion

    //region Error handling
    private void accountCreationError(ShootrException error) {
        if (error instanceof DomainValidationException) {
            DomainValidationException validationException = (DomainValidationException) error;
            List<FieldValidationError> errors = validationException.getErrors();
            showValidationErrors(errors);
        } else if (error instanceof ServerCommunicationException) {
            onCommunicationError();
        } else {
            Timber.e(error, "Unknown error creating account.");
            showViewError(errorMessageFactory.getUnknownErrorMessage());
        }
    }

    private void showValidationErrors(List<FieldValidationError> errors) {
        for (FieldValidationError validationError : errors) {
            String errorMessage = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
            switch (validationError.getField()) {
                case CreateUserValidator.FIELD_EMAIL:
                    showViewEmailError(errorMessage);
                    break;
                case CreateUserValidator.FIELD_USERNAME:
                    showViewUsernameError(errorMessage);
                    break;
                case CreateUserValidator.FIELD_PASSWORD:
                    showViewPasswordError(errorMessage);
                    break;
                default:
                    showViewError(errorMessage);
            }
        }
    }

    private void showViewEmailError(String errorMessage) {
        emailRegistrationView.showEmailError(errorMessage);
    }

    private void showViewUsernameError(String errorMessage) {
        emailRegistrationView.showUsernameError(errorMessage);
    }

    private void showViewPasswordError(String errorMessage) {
        emailRegistrationView.showPasswordError(errorMessage);
    }

    private void onCommunicationError() {
        showViewError(errorMessageFactory.getCommunicationErrorMessage());
    }

    private void showViewError(String errorMessage) {
        emailRegistrationView.showError(errorMessage);
    }
    //endregion

    private Boolean validateAllFieldsBeforeCreationAttemptOrShowErrors() {
        boolean validationSuccessful = true;
        if (!validateFieldOrShowError(CreateUserValidator.FIELD_EMAIL)) {
            emailRegistrationView.focusOnEmailField();
            validationSuccessful = false;
        }
        if (!validateFieldOrShowError(CreateUserValidator.FIELD_USERNAME)) {
            emailRegistrationView.focusOnUsernameField();
            validationSuccessful = false;
        }
        if (!validateFieldOrShowError(CreateUserValidator.FIELD_PASSWORD)) {
            emailRegistrationView.focusOnPasswordField();
            validationSuccessful = false;
        }
        return validationSuccessful;
    }

    private Boolean validateFieldOrShowError(int field) {
        String email = emailRegistrationView.getEmailInput();
        String username = emailRegistrationView.getUsernameInput();
        String password = emailRegistrationView.getPasswordInput();
        List<FieldValidationError> errors = new CreateUserValidator().validate(email, username, password);
        List<FieldValidationError> fieldErrors = new ArrayList<>();
        for (FieldValidationError error : errors) {
            if (error.getField() == field) {
                fieldErrors.add(error);
            }
        }
        showValidationErrors(fieldErrors);
        return fieldErrors.size() == 0;
    }

    private void showViewLoading() {
        emailRegistrationView.showLoading();
        emailRegistrationView.hideCreateButton();
    }

    private void hideViewLoading() {
        emailRegistrationView.hideLoading();
        emailRegistrationView.showCreateButton();
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void dontConfirmAccountCreation() {
        emailRegistrationView.focusOnEmailField();
    }
}
