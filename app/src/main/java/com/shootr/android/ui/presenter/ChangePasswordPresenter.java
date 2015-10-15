package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ChangePasswordInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ChangePasswordInvalidException;
import com.shootr.android.domain.validation.ChangePasswordValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.views.ChangePasswordView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ChangePasswordPresenter implements Presenter {

    private final ErrorMessageFactory errorMessageFactory;
    private final ChangePasswordInteractor changePasswordInteractor;
    private final LogoutInteractor logoutInteractor;
    private final SessionRepository sessionRepository;

    private ChangePasswordView changePasswordView;

    @Inject public ChangePasswordPresenter(ErrorMessageFactory errorMessageFactory,
      ChangePasswordInteractor changePasswordInteractor, LogoutInteractor logoutInteractor,
      SessionRepository sessionRepository) {
        this.errorMessageFactory = errorMessageFactory;
        this.changePasswordInteractor = changePasswordInteractor;
        this.logoutInteractor = logoutInteractor;
        this.sessionRepository = sessionRepository;
    }

    protected void setView(ChangePasswordView changePasswordView) {
        this.changePasswordView = changePasswordView;
    }

    public void initialize(ChangePasswordView changePasswordView) {
        setView(changePasswordView);
    }

    public void attempToChangePassword(String currentPassword, String newPassword, String newPasswordAgain) {
        if(validatePasswords(currentPassword, newPassword, newPasswordAgain)) {
            changePasswordInteractor.attempToChangePassword(currentPassword,
              newPassword,
              new Interactor.CompletedCallback() {
                  @Override public void onCompleted() {
                      changePasswordView.showLogoutInProgress();
                      logoutInteractor.attempLogout(new Interactor.CompletedCallback() {
                          @Override public void onCompleted() {
                                changePasswordView.navigateToWelcomeScreen();
                          }
                      }, new Interactor.ErrorCallback() {
                          @Override public void onError(ShootrException error) {
                              changePasswordView.hideLogoutInProgress();
                              showErrorInView(error);
                          }
                      });
                  }
              },
              new Interactor.ErrorCallback() {
                  @Override public void onError(ShootrException error) {
                      showErrorInView(error);
                  }
              });
        }

    }

    private void showErrorInView(ShootrException error) {
        changePasswordView.showError(errorMessageFactory.getMessageForError(error));
    }

    private boolean validatePasswords(String currentPassword, String newPassword, String newPasswordAgain) {
        boolean validationSuccessful = true;
        if (!validateFieldOrShowError(currentPassword, newPassword, newPasswordAgain)) {
            validationSuccessful = false;
        }
        return validationSuccessful;
    }

    private boolean validateFieldOrShowError(String currentPassword, String newPassword, String newPasswordAgain) {
        List<FieldValidationError> errors = new ChangePasswordValidator().validate(currentPassword, newPassword, newPasswordAgain, sessionRepository.getCurrentUser().getUsername());
        showValidationErrors(errors);
        return errors.size() == 0;
    }

    private void showValidationErrors(List<FieldValidationError> errors) {
        for (FieldValidationError validationError : errors) {
            String errorMessage = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
            switch (validationError.getField()) {
                case ChangePasswordValidator.FIELD_CURRENT_PASSWORD:
                    showViewCurrentPasswordError(errorMessage);
                    break;
                case ChangePasswordValidator.FIELD_NEW_PASSWORD:
                    showViewNewPasswordError(errorMessage);
                    break;
                case ChangePasswordValidator.FIELD_NEW_PASSWORD_AGAIN:
                    showViewNewPasswordAgainError(errorMessage);
                    break;
                default:
                    showViewError(errorMessage);
            }
        }
    }

    private void showViewCurrentPasswordError(String errorMessage) {
        changePasswordView.showCurrentPasswordError(errorMessage);
    }

    private void showViewNewPasswordError(String errorMessage) {
        changePasswordView.showNewPasswordError(errorMessage);
    }

    private void showViewNewPasswordAgainError(String errorMessage) {
        changePasswordView.showNewPasswordAgainError(errorMessage);
    }

    private void showViewError(String errorMessage) {
        changePasswordView.showError(errorMessage);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
