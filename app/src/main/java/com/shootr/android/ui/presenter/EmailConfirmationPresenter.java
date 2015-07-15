package com.shootr.android.ui.presenter;

import com.shootr.android.domain.validation.EmailConfirmationValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.views.EmailConfirmationView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EmailConfirmationPresenter implements Presenter {

    private final ErrorMessageFactory errorMessageFactory;

    private EmailConfirmationView emailConfirmationView;

    @Inject public EmailConfirmationPresenter(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(EmailConfirmationView emailConfirmationView) {
        this.emailConfirmationView = emailConfirmationView;
    }

    public void initialize(EmailConfirmationView emailConfirmationView, String email) {
        this.setView(emailConfirmationView);
        this.setUserEmail(email);
        this.confirmEmail(email);
    }

    private void confirmEmail(String email) {
        emailConfirmationView.showConfirmationToUser(email);
    }

    public void setUserEmail(String userEmail) {
        emailConfirmationView.showUserEmail(userEmail);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void onEmailEdited(String editedEmail) {
        if(verifyEmailBeforeConfirmating(editedEmail)) {
            emailConfirmationView.updateDoneButton();
        }
    }

    private boolean verifyEmailBeforeConfirmating(String editedEmail) {
        boolean validationSuccessful = true;
        if (!validateFieldOrShowError(editedEmail, EmailConfirmationValidator.FIELD_EMAIL)) {
            //emailConfirmationView.focusOnEmailField();
            validationSuccessful = false;
        }
        return validationSuccessful;
    }

    private Boolean validateFieldOrShowError(String editedEmail, int field) {
        List<FieldValidationError> errors = new EmailConfirmationValidator().validate(editedEmail);
        List<FieldValidationError> fieldErrors = new ArrayList<>();
        for (FieldValidationError error : errors) {
            if (error.getField() == field) {
                fieldErrors.add(error);
            }
        }
        showValidationErrors(fieldErrors);
        return fieldErrors.size() == 0;
    }

    private void showValidationErrors(List<FieldValidationError> errors) {
        for (FieldValidationError validationError : errors) {
            String errorMessage = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
            switch (validationError.getField()) {
                case EmailConfirmationValidator.FIELD_EMAIL:
                    showViewEmailError(errorMessage);
                    break;
                default:
                    showViewError(errorMessage);
            }
        }
    }

    private void showViewError(String errorMessage) {
        emailConfirmationView.showError(errorMessage);
    }

    private void showViewEmailError(String errorMessage) {
        emailConfirmationView.showEmailError(errorMessage);
    }
}
