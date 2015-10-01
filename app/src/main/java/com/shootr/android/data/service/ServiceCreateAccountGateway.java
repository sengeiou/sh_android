package com.shootr.android.data.service;

import android.support.annotation.NonNull;
import com.shootr.android.data.api.entity.CreateAccountApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.UsernameAlreadyExistsException;
import com.shootr.android.domain.service.user.CreateAccountGateway;
import java.io.IOException;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class ServiceCreateAccountGateway implements CreateAccountGateway {

    private final AuthApiService authApiService;
    private final UserEntityMapper userEntityMapper;

    @Inject
    public ServiceCreateAccountGateway(AuthApiService authApiService, UserEntityMapper userEntityMapper) {
        this.authApiService = authApiService;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public LoginResult performCreateAccount(String username, String email, String password)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        try {
            return sendCreateAccount(username, email, password);
        } catch (ApiException apiException) {
            if (ErrorInfo.EmailAlreadyExistsException == apiException.getErrorInfo()) {
                throw new EmailAlreadyExistsException();
            } else if (ErrorInfo.UserNameAlreadyExistsException == apiException.getErrorInfo()) {
                throw new UsernameAlreadyExistsException();
            } else {
                throw new ServerCommunicationException(apiException);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    @NonNull
    protected LoginResult sendCreateAccount(String username, String email, String password)
      throws ApiException, IOException {
        CreateAccountApiEntity createAccountApiEntity = new CreateAccountApiEntity(username, email, password);
        UserEntity newLoggedInUser = authApiService.createAccount(createAccountApiEntity);
        checkNotNull(newLoggedInUser);
        User loggedInUser = userEntityMapper.transform(newLoggedInUser);
        String sessionToken = newLoggedInUser.getSessionToken();
        return new LoginResult(loggedInUser, sessionToken);
    }
}
