package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import java.io.IOException;

public interface CreateAccountGateway {

    LoginResult performCreateAccount(String username, String email, String password) throws IOException;
}
