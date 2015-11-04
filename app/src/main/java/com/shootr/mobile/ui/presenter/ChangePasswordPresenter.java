package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ChangePasswordInteractor;
import com.shootr.mobile.domain.interactor.user.RemoveSessionDataInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.validation.ChangePasswordValidator;
import com.shootr.mobile.domain.validation.FieldValidationError;
import com.shootr.mobile.ui.views.ChangePasswordView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ChangePasswordPresenter implements Presenter {

    private final ErrorMessageFactory errorMessageFactory;
    private final ChangePasswordInteractor changePasswordInteractor;
    private final RemoveSessionDataInteractor removeSessionDataInteractor;
    private final SessionRepository sessionRepository;

    private ChangePasswordView changePasswordView;

    @Inject public ChangePasswordPresenter(ErrorMessageFactory errorMessageFactory,
      ChangePasswordInteractor changePasswordInteractor, RemoveSessionDataInteractor removeSessionDataInteractor,
      SessionRepository sessionRepository) {
        this.errorMessageFactory = errorMessageFactory;
        this.changePasswordInteractor = changePasswordInteractor;
        this.removeSessionDataInteractor = removeSessionDataInteractor;
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
                      removeSessionDataInteractor.removeData(new Interactor.CompletedCallback() {
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
