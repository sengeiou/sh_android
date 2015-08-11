package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ChangeEmailInteractor;
import com.shootr.android.domain.interactor.user.ConfirmEmailInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.EmailInUseException;
import com.shootr.android.domain.validation.EmailConfirmationValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EmailConfirmationView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EmailConfirmationPresenter implements Presenter {

    private final ErrorMessageFactory errorMessageFactory;
    private final ConfirmEmailInteractor confirmEmailInteractor;
    private final ChangeEmailInteractor changeEmailInteractor;
    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;

    private EmailConfirmationView emailConfirmationView;
    private String currentEmail;

    @Inject public EmailConfirmationPresenter(ErrorMessageFactory errorMessageFactory,
      ConfirmEmailInteractor confirmEmailInteractor, ChangeEmailInteractor changeEmailInteractor,
      SessionRepository sessionRepository, UserModelMapper userModelMapper) {
        this.errorMessageFactory = errorMessageFactory;
        this.confirmEmailInteractor = confirmEmailInteractor;
        this.changeEmailInteractor = changeEmailInteractor;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(EmailConfirmationView emailConfirmationView) {
        this.emailConfirmationView = emailConfirmationView;
    }

    protected void setCurrentEmail(String userEmail) {
        this.currentEmail = userEmail;
        emailConfirmationView.showUserEmail(userEmail);
    }

    public void initialize(EmailConfirmationView emailConfirmationView, String email) {
        this.setView(emailConfirmationView);
        this.setCurrentEmail(email);
        this.requestEmailConfirmataionIfNotConfirmed();
    }

    protected void requestEmailConfirmataionIfNotConfirmed() {
        UserModel currentUserModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        if (!currentUserModel.isEmailConfirmed()) {
            requestEmailConfirmation(currentEmail);
        }
    }

    private void requestEmailConfirmation(final String email) {
        confirmEmailInteractor.confirmEmail(new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                emailConfirmationView.showConfirmationAlertToUser(email);
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void onEmailEdited(String editedEmail) {
        if (!editedEmail.equals(currentEmail)) {
            boolean isValidEmail = validateEmailAndShowErrors(editedEmail);
            if (isValidEmail) {
                emailConfirmationView.showDoneButton();
            } else {
                emailConfirmationView.hideDoneButton();
            }
        } else {
            emailConfirmationView.hideDoneButton();
        }
    }

    private boolean validateEmailAndShowErrors(String editedEmail) {
        List<FieldValidationError> errors = new EmailConfirmationValidator().validate(editedEmail);
        if (errors.isEmpty()) {
            return true;
        } else {
            showValidationErrors(errors);
            return false;
        }
    }

    private void showValidationErrors(List<FieldValidationError> errors) {
        for (FieldValidationError validationError : errors) {
            String errorMessage = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
            switch (validationError.getField()) {
                case EmailConfirmationValidator.FIELD_EMAIL:
                    showViewEmailError(errorMessage);
                    break;
                default:
                    emailConfirmationView.showError(errorMessage);
            }
        }
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        } else if (error instanceof EmailInUseException) {
            errorMessage = "Email already registered";
        } else {
            errorMessage = errorMessageFactory.getMessageForError(error);
        }
        emailConfirmationView.showError(errorMessage);
    }

    private void showViewEmailError(String errorMessage) {
        emailConfirmationView.showEmailError(errorMessage);
    }

    public void done(String editedEmail) {
        if(validateEmailAndShowErrors(editedEmail) && !editedEmail.equals(currentEmail)) {
            changeEmail(editedEmail);
            emailConfirmationView.hideDoneButton();
        } else {
            emailConfirmationView.closeScreen();
        }
    }

    private void changeEmail(final String emailEdited) {
        changeEmailInteractor.changeEmail(emailEdited, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                requestEmailConfirmation(emailEdited);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
