package com.shootr.android.domain.service.user;

import java.io.IOException;

public interface CreateAccountGateway {

    void performCreateAccount(String username, String email, String password) throws IOException;
}
