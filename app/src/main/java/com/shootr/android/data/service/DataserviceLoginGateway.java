package com.shootr.android.data.service;

import com.shootr.android.data.api.entity.FacebookLoginApiEntity;
import com.shootr.android.data.api.entity.LoginApiEntity;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.service.user.LoginGateway;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceLoginGateway implements LoginGateway {

    private final ShootrService shootrService;
    private final AuthApiService authApiService;
    private final UserEntityMapper userEntityMapper;
    private final DeviceManager deviceManager;

    @Inject public DataserviceLoginGateway(ShootrService shootrService,
      AuthApiService authApiService,
      UserEntityMapper userEntityMapper,
      DeviceManager deviceManager) {
        this.shootrService = shootrService;
        this.authApiService = authApiService;
        this.userEntityMapper = userEntityMapper;
        this.deviceManager = deviceManager;
    }

    @Override public LoginResult performLogin(String usernameOrEmail, String password) throws IOException {
        UserEntity loggedInUserEntity = loginWithUsernameOrEmail(usernameOrEmail, password);
        User loggedInUser = userEntityMapper.transform(loggedInUserEntity);
        String sessionToken = loggedInUserEntity.getSessionToken();
        return new LoginResult(loggedInUser, sessionToken);
    }

    @Override
    public LoginResult performFacebookLogin(String facebookToken) throws IOException {
        UserEntity loggedInUserEntity = authApiService.authenticateWithFacebook(new FacebookLoginApiEntity(facebookToken));
        User loggedInUser = userEntityMapper.transform(loggedInUserEntity);
        String sessionToken = loggedInUserEntity.getSessionToken();
        return new LoginResult(loggedInUser, sessionToken);
    }

    protected UserEntity loginWithUsernameOrEmail(String usernameOrEmail, String password) throws IOException {
        LoginApiEntity loginApiEntity = new LoginApiEntity();
        loginApiEntity.setPassword(password);

        if (usernameOrEmail.contains("@")) {
            loginApiEntity.setEmail(usernameOrEmail);
        } else {
            loginApiEntity.setUserName(usernameOrEmail);
        }

        return authApiService.authenticate(loginApiEntity);
    }

    @Override public void performLogout(String idUser) throws IOException {
        DeviceEntity deviceByIdUser = deviceManager.getDeviceByIdUser(idUser);
        if(deviceByIdUser != null) {
            shootrService.logout(idUser, deviceByIdUser.getIdDevice());
        }
    }
}
