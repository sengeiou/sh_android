package com.shootr.mobile.data.service;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.entity.CreateAccountApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.AuthApiService;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.domain.model.user.LoginResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.service.user.CreateAccountGateway;
import java.io.IOException;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ServiceCreateAccountGateway implements CreateAccountGateway {

    private final AuthApiService authApiService;
    private final UserEntityMapper userEntityMapper;

    @Inject public ServiceCreateAccountGateway(AuthApiService authApiService, UserEntityMapper userEntityMapper) {
        this.authApiService = authApiService;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public LoginResult performCreateAccount(String username, String email, String password, String locale)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        try {
            return sendCreateAccount(username, email, password, locale);
        } catch (ApiException apiException) {
            if (ErrorInfo.EmailAlreadyExistsException == apiException.getErrorInfo()) {
                throw new EmailAlreadyExistsException(apiException);
            } else if (ErrorInfo.UserNameAlreadyExistsException == apiException.getErrorInfo()) {
                throw new UsernameAlreadyExistsException(apiException);
            } else {
                throw new ServerCommunicationException(apiException);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    @NonNull protected LoginResult sendCreateAccount(String username, String email, String password, String locale)
      throws ApiException, IOException {
        CreateAccountApiEntity createAccountApiEntity = new CreateAccountApiEntity(username, email, password, locale);
        UserEntity newLoggedInUser = authApiService.createAccount(createAccountApiEntity);
        checkNotNull(newLoggedInUser);
        User loggedInUser = userEntityMapper.transform(newLoggedInUser);
        String sessionToken = newLoggedInUser.getSessionToken();
        return new LoginResult(loggedInUser, sessionToken);
    }
}
