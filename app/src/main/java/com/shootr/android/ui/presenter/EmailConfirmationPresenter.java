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
    private String initializedEmail;

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

    protected void setUserEmail(String userEmail) {
        this.initializedEmail = userEmail;
        emailConfirmationView.showUserEmail(userEmail);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void onEmailEdited(String editedEmail) {
        if(!editedEmail.equals(initializedEmail) && verifyEmailBeforeConfirmating(editedEmail)) {
            emailConfirmationView.updateDoneButton();
        } else if (!verifyEmailBeforeConfirmating(editedEmail)) {
            emailConfirmationView.hideDoneButton();
        }
    }

    private boolean verifyEmailBeforeConfirmating(String editedEmail) {
        boolean validationSuccessful = true;
        if (!validateFieldOrShowError(editedEmail, EmailConfirmationValidator.FIELD_EMAIL)) {
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

    public void attempToConfirmEmail(String emailEdited) {
        if(verifyEmailBeforeConfirmating(emailEdited)) {
            emailConfirmationView.showConfirmationToUser(emailEdited);
            emailConfirmationView.hideDoneButton();
        }
    }
}
