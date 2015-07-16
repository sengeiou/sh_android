package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.domain.exception.InvalidEmailConfirmationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
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

    @Override public void changeEmail(String email) {
        //TODO
    }
}
