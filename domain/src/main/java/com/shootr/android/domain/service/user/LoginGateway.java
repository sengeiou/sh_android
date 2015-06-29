package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import java.io.IOException;

public interface LoginGateway {

    LoginResult performLogin(String usernameOrEmail, String password) throws IOException;

    void performLogout(String idUser) throws IOException;
}
