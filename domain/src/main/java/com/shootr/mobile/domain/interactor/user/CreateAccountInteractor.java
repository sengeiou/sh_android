package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.domain.validation.CreateUserValidator;
import com.shootr.mobile.domain.validation.FieldValidationError;
import java.util.List;
import javax.inject.Inject;

public class CreateAccountInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrUserService shootrUserService;
  private final LocaleProvider localeProvider;

  private String email;
  private String username;
  private String password;
  private CompletedCallback callback;
  private ErrorCallback errorCallback;

  @Inject public CreateAccountInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrUserService shootrUserService,
      LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.shootrUserService = shootrUserService;
    this.localeProvider = localeProvider;
  }

  public void createAccount(String email, String username, String password,
      CompletedCallback completedCallback, ErrorCallback errorCallback) {
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
        shootrUserService.createAccount(username, email, password, localeProvider.getLocale());
        notifyLoaded();
      } catch (MassiveRegisterErrorException e) {
        notifyError(e);
      } catch (EmailAlreadyExistsException e) {
        handleServerError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE,
            CreateUserValidator.FIELD_EMAIL);
      } catch (UsernameAlreadyExistsException e) {
        handleServerError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE,
            CreateUserValidator.FIELD_USERNAME);
      } catch (ServerCommunicationException communicationError) {
        notifyError(communicationError);
      }
    }
  }

  protected void handleServerError(String errorCode, int field) {
    FieldValidationError fieldValidationError = new FieldValidationError(errorCode, field);
    notifyError(new DomainValidationException(fieldValidationError));
  }

  private boolean validateInput() {
    List<FieldValidationError> validationErrors =
        new CreateUserValidator().validate(email, username, password);
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
}
