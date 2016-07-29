package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.model.user.LoginResult;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;

public interface CreateAccountGateway {

    LoginResult performCreateAccount(String username, String email, String password, String language)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException;
}
