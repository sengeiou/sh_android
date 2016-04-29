package com.shootr.mobile.data.service;

import com.shootr.mobile.data.api.entity.ChangeEmailApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.AuthApiService;
import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.InvalidEmailConfirmationException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.service.user.ConfirmEmailGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceConfirmEmailGateway implements ConfirmEmailGateway {

    private final AuthApiService authApiService;

    @Inject public ServiceConfirmEmailGateway(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    @Override public void confirmEmail() throws InvalidEmailConfirmationException {
        try {
            this.authApiService.confirmEmail();
        } catch (ApiException error) {
            throw new InvalidEmailConfirmationException(error);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException, UnauthorizedRequestException {
        ChangeEmailApiEntity changeEmailApiEntity = new ChangeEmailApiEntity();
        changeEmailApiEntity.setNewEmail(email);
        try {
            this.authApiService.changeEmail(changeEmailApiEntity);
        } catch (ApiException error) {
            captureApiException(error);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    public void captureApiException(ApiException error)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException, UnauthorizedRequestException {
        if (ErrorInfo.EmailAlreadyExistsException == error.getErrorInfo()) {
            throw new EmailAlreadyExistsException(error);
        } else if (ErrorInfo.EmailMatchNewEmailException == error.getErrorInfo()) {
            throw new EmailAlreadyConfirmedException(error);
        } else if (ErrorInfo.InsufficientAuthenticationException == error.getErrorInfo()) {
            throw new UnauthorizedRequestException(error);
        } else {
            throw new ServerCommunicationException(error);
        }
    }
}
