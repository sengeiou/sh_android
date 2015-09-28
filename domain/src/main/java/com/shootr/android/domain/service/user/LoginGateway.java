package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.exception.InvalidLoginException;
import java.io.IOException;

public interface LoginGateway {

    LoginResult performLogin(String usernameOrEmail, String password) throws InvalidLoginException;

    LoginResult performFacebookLogin(String facebookToken) throws InvalidLoginException;

    void performLogout(String idUser);
}
