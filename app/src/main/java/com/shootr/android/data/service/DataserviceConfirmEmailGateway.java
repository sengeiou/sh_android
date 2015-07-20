package com.shootr.android.data.service;

import com.shootr.android.data.api.entity.ChangeEmailApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.EmailMatchNewEmailException;
import com.shootr.android.domain.exception.InvalidChangeEmailException;
import com.shootr.android.domain.exception.InvalidEmailConfirmationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.UsernameAlreadyExistsException;
import com.shootr.android.domain.service.user.ConfirmEmailGateway;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceConfirmEmailGateway implements ConfirmEmailGateway {

    private final AuthApiService authApiService;

    @Inject public DataserviceConfirmEmailGateway(AuthApiService authApiService) {
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
      throws InvalidChangeEmailException, EmailAlreadyExistsException, EmailMatchNewEmailException {
        ChangeEmailApiEntity changeEmailApiEntity = new ChangeEmailApiEntity();
        changeEmailApiEntity.setNewEmail(email);
        try {
            this.authApiService.changeEmail(changeEmailApiEntity);
        }catch (ApiException error) {
            if (ErrorInfo.EmailAlreadyExistsException == error.getErrorInfo()) {
                throw new EmailAlreadyExistsException();
            } else if(ErrorInfo.EmailMatchNewEmailException == error.getErrorInfo()) {
                throw new EmailMatchNewEmailException();
            } else {
                throw new InvalidChangeEmailException();
            }
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}
