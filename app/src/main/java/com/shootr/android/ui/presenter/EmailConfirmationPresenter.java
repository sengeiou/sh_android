package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ChangeEmailInteractor;
import com.shootr.android.domain.interactor.user.ConfirmEmailInteractor;
import com.shootr.android.domain.interactor.user.UpdateUserInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.EmailInUseException;
import com.shootr.android.domain.service.ResetPasswordException;
import com.shootr.android.domain.validation.EmailConfirmationValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EmailConfirmationView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EmailConfirmationPresenter implements Presenter {

    private final ErrorMessageFactory errorMessageFactory;
    private final ConfirmEmailInteractor confirmEmailInteractor;
    private final ChangeEmailInteractor changeEmailInteractor;
    private final UpdateUserInteractor updateUserInteractor;
    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;

    private EmailConfirmationView emailConfirmationView;
    private String initializedEmail;

    @Inject public EmailConfirmationPresenter(ErrorMessageFactory errorMessageFactory,
      ConfirmEmailInteractor confirmEmailInteractor, ChangeEmailInteractor changeEmailInteractor,
      UpdateUserInteractor updateUserInteractor, SessionRepository sessionRepository, UserModelMapper userModelMapper) {
        this.errorMessageFactory = errorMessageFactory;
        this.confirmEmailInteractor = confirmEmailInteractor;
        this.changeEmailInteractor = changeEmailInteractor;
        this.updateUserInteractor = updateUserInteractor;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(EmailConfirmationView emailConfirmationView) {
        this.emailConfirmationView = emailConfirmationView;
    }

    public void initialize(EmailConfirmationView emailConfirmationView, String email) {
        this.setView(emailConfirmationView);
        this.setUserEmail(email);
        this.confirmEmail(email);
    }

    protected void confirmEmail(final String email) {
        updateUserInteractor.updateCurrentUser(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                UserModel currentUserModel = userModelMapper.transform(sessionRepository.getCurrentUser());
                if (!currentUserModel.getEmailConfirmed()) {
                    confirmEmailInteractor.confirmEmail(new Interactor.CompletedCallback() {
                        @Override public void onCompleted() {
                            emailConfirmationView.showConfirmationToUser(email);
                        }
                    }, new Interactor.ErrorCallback() {
                        @Override public void onError(ShootrException error) {
                            showViewError(error);
                        }
                    });
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                emailConfirmationView.showError(error.getMessage());
            }
        });
    }

    protected void setUserEmail(String userEmail) {
        this.initializedEmail = userEmail;
        emailConfirmationView.showUserEmail(userEmail);
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

    public void attempToChangeEmail(String emailEdited) {
        if(verifyEmailBeforeConfirmating(emailEdited) && !emailEdited.equals(initializedEmail)) {
            changeEmail(emailEdited);
            emailConfirmationView.hideDoneButton();
        } else {
            emailConfirmationView.goBack();
        }
    }

    private void changeEmail(final String emailEdited) {
        changeEmailInteractor.changeEmail(emailEdited, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                confirmEmail(emailEdited);
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
