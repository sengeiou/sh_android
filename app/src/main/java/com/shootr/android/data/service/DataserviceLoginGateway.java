package com.shootr.android.data.service;

import com.shootr.android.data.api.entity.FacebookLoginApiEntity;
import com.shootr.android.data.api.entity.LoginApiEntity;
import com.shootr.android.data.api.entity.LogoutApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.AuthApiService;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.FacebookUserEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidLoginException;
import com.shootr.android.domain.exception.ServerCommunicationException;
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

    @Override public LoginResult performLogin(String usernameOrEmail, String password) throws InvalidLoginException {
        try {
            UserEntity loggedInUserEntity = loginWithUsernameOrEmail(usernameOrEmail, password);
            User loggedInUser = userEntityMapper.transform(loggedInUserEntity);
            String sessionToken = loggedInUserEntity.getSessionToken();
            return new LoginResult(loggedInUser, sessionToken);
        } catch (ApiException error) {
            throw new InvalidLoginException();
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override
    public LoginResult performFacebookLogin(String facebookToken) throws InvalidLoginException {
        try {
            FacebookUserEntity loggedInUserEntity =
              authApiService.authenticateWithFacebook(new FacebookLoginApiEntity(facebookToken));
            User loggedInUser = userEntityMapper.transform(loggedInUserEntity);
            String sessionToken = loggedInUserEntity.getSessionToken();
            LoginResult loginResult = new LoginResult(loggedInUser, sessionToken);
            if (loggedInUserEntity.isNewUser() != null && loggedInUserEntity.isNewUser() == 1) {
                loginResult.setIsNewUser(true);
            } else {
                loginResult.setIsNewUser(false);
            }
            return loginResult;
        } catch (ApiException apiError) {
            throw new InvalidLoginException();
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    protected UserEntity loginWithUsernameOrEmail(String usernameOrEmail, String password)
      throws IOException, ApiException {
        LoginApiEntity loginApiEntity = new LoginApiEntity();
        loginApiEntity.setPassword(password);

        if (usernameOrEmail.contains("@")) {
            loginApiEntity.setEmail(usernameOrEmail);
        } else {
            loginApiEntity.setUserName(usernameOrEmail);
        }

        return authApiService.authenticate(loginApiEntity);
    }

    @Override public void performLogout(String idUser) {
        try {
            DeviceEntity deviceByIdUser = deviceManager.getDeviceByIdUser(idUser);
            if(deviceByIdUser != null) {
                authApiService.logout(new LogoutApiEntity(deviceByIdUser.getIdDevice()));
            }
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
