package com.shootr.android.domain.service.user;

import java.io.IOException;

public interface ResetPasswordEmailGateway {

    void sendPasswordResetEmail(String idUser) throws IOException;
}
