package com.shootr.android.data.service;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.user.LoginException;
import com.shootr.android.domain.service.user.LoginGateway;
import com.shootr.android.exception.ServerException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceLoginGateway implements LoginGateway {

    private final ShootrService shootrService;
    private final UserEntityMapper userEntityMapper;

    @Inject public DataserviceLoginGateway(ShootrService shootrService, UserEntityMapper userEntityMapper) {
        this.shootrService = shootrService;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public LoginResult performLogin(String usernameOrEmail, String password) throws IOException {
        try {
            UserEntity loggedInUserEntity = shootrService.login(usernameOrEmail, password);
            User loggedInUser = userEntityMapper.transform(loggedInUserEntity);
            String sessionToken = loggedInUserEntity.getSessionToken();
            return new LoginResult(loggedInUser, sessionToken);
        } catch (ServerException e) {
            if (ServerException.G025.equals(e.getErrorCode())) {
                throw new LoginException(e);
            } else {
                throw new ServerCommunicationException(e);
            }
        }
    }
}
