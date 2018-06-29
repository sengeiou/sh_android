package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.user.LoginResult;

public interface CreateAccountGateway {

  LoginResult performCreateAccount(String username, String email, String password,
      boolean privacyAccepted, String language)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException,
      MassiveRegisterErrorException;
}
