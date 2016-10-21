package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForFacebookException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForShootrException;
import com.shootr.mobile.domain.model.user.LoginResult;

public interface LoginGateway {

    LoginResult performLogin(String usernameOrEmail, String password) throws InvalidLoginException,
        InvalidLoginMethodForShootrException;

    LoginResult performFacebookLogin(String facebookToken, String language) throws InvalidLoginException,
        InvalidLoginMethodForFacebookException;

    void performLogout(String idUser);
}
