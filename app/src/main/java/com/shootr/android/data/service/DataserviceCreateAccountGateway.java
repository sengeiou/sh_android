package com.shootr.android.data.service;

import com.shootr.android.data.api.entity.CreateAccountApiEntity;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.service.user.CreateAccountGateway;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceCreateAccountGateway implements CreateAccountGateway {

    private final AuthApiService authApiService;
    private final UserEntityMapper userEntityMapper;

    @Inject
    public DataserviceCreateAccountGateway(AuthApiService authApiService, UserEntityMapper userEntityMapper) {
        this.authApiService = authApiService;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public LoginResult performCreateAccount(String username, String email, String password) throws IOException {
        UserEntity newLoggedInUser = authApiService.createAccount(new CreateAccountApiEntity(username, email, password));
        User loggedInUser = userEntityMapper.transform(newLoggedInUser);
        String sessionToken = newLoggedInUser.getSessionToken();
        return new LoginResult(loggedInUser, sessionToken);
    }
}
