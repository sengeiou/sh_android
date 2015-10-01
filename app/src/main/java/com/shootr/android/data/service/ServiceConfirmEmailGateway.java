package com.shootr.android.data.service;

import com.shootr.android.data.api.entity.ChangeEmailApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.InvalidEmailConfirmationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.UnauthorizedRequestException;
import com.shootr.android.domain.service.user.ConfirmEmailGateway;
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
        }catch (ApiException error) {
            throw new InvalidEmailConfirmationException();
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException,
      UnauthorizedRequestException {
        ChangeEmailApiEntity changeEmailApiEntity = new ChangeEmailApiEntity();
        changeEmailApiEntity.setNewEmail(email);
        try {
            this.authApiService.changeEmail(changeEmailApiEntity);
        }catch (ApiException error) {
            if (ErrorInfo.EmailAlreadyExistsException == error.getErrorInfo()) {
                throw new EmailAlreadyExistsException();
            } else if(ErrorInfo.EmailMatchNewEmailException == error.getErrorInfo()) {
                throw new EmailAlreadyConfirmedException();
            } else if(ErrorInfo.InsufficientAuthenticationException == error.getErrorInfo()) {
                throw new UnauthorizedRequestException();
            } else {
                throw new ServerCommunicationException(error);
            }
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}
