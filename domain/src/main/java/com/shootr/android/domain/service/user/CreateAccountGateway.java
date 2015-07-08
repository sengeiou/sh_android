package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.UsernameAlreadyExistsException;
import java.io.IOException;

public interface CreateAccountGateway {

    LoginResult performCreateAccount(String username, String email, String password)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException;
}
