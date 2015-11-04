package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.LoginResult;
import com.shootr.mobile.domain.exception.InvalidLoginException;

public interface LoginGateway {

    LoginResult performLogin(String usernameOrEmail, String password) throws InvalidLoginException;

    LoginResult performFacebookLogin(String facebookToken) throws InvalidLoginException;

    void performLogout(String idUser);
}
